package com.algaworks.brewer.repository.helper.venda;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.MonthDay;
import java.time.Year;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.algaworks.brewer.dto.VendaMes;
import com.algaworks.brewer.dto.VendaOrigem;
import com.algaworks.brewer.model.StatusVenda;
import com.algaworks.brewer.model.TipoPessoa;
import com.algaworks.brewer.model.Usuario;
import com.algaworks.brewer.model.Venda;
import com.algaworks.brewer.repository.filter.VendaFilter;
import com.algaworks.brewer.repository.paginacao.PaginacaoUtil;

public class VendasImpl implements VendasQueries {

	@PersistenceContext
	private EntityManager manager;

	@Autowired
	private PaginacaoUtil paginacaoUtil;

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	@Override
	public Page<Venda> filtrar(VendaFilter vendaFilter, Pageable pageable) {
		Criteria criteria = manager.unwrap(Session.class).createCriteria(Venda.class);
		paginacaoUtil.preparar(criteria, pageable);
		adicionarFiltro(vendaFilter, criteria);
		return new PageImpl<>(criteria.list(), pageable, total(vendaFilter));
	}

	@Transactional(readOnly = true)
	@Override
	public Venda buscarComItens(Long codigo) {
		Criteria criteria = manager.unwrap(Session.class).createCriteria(Venda.class);
		criteria.createAlias("itens", "i", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq("codigo", codigo));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		return (Venda) criteria.uniqueResult();
	}

	private Long total(VendaFilter vendaFilter) {
		Criteria criteria = manager.unwrap(Session.class).createCriteria(Venda.class);
		adicionarFiltro(vendaFilter, criteria);
		criteria.setProjection(Projections.rowCount());

		return (Long) criteria.uniqueResult();
	}

	private void adicionarFiltro(VendaFilter vendaFilter, Criteria criteria) {
		criteria.createAlias("cliente", "c");

		if (vendaFilter != null) {

			if (!StringUtils.isEmpty(vendaFilter.getCodigo())) {
				criteria.add(Restrictions.eq("codigo", vendaFilter.getCodigo()));
			}

			if (vendaFilter.getStatus() != null) {
				criteria.add(Restrictions.eq("status", vendaFilter.getStatus()));
			}

			if (vendaFilter.getDesde() != null) {
				LocalDateTime desde = LocalDateTime.of(vendaFilter.getDesde(), LocalTime.of(0, 0));
				criteria.add(Restrictions.ge("dataCriacao", desde));
			}

			if (vendaFilter.getAte() != null) {
				LocalDateTime ate = LocalDateTime.of(vendaFilter.getAte(), LocalTime.of(23, 59));
				criteria.add(Restrictions.le("dataCriacao", ate));
			}

			if (vendaFilter.getValorMinimo() != null) {
				criteria.add(Restrictions.ge("valorTotal", vendaFilter.getValorMinimo()));
			}

			if (vendaFilter.getValorMaximo() != null) {
				criteria.add(Restrictions.le("valorTotal", vendaFilter.getValorMaximo()));
			}

			if (!StringUtils.isEmpty(vendaFilter.getNomeCliente())) {
				criteria.add(Restrictions.ilike("c.nome", vendaFilter.getNomeCliente()));
			}

			if (!StringUtils.isEmpty(vendaFilter.getCpfOuCnpjCliente())) {
				criteria.add(Restrictions.eq("c.cpfOuCnpj",
						TipoPessoa.removerFormatacao(vendaFilter.getCpfOuCnpjCliente())));
			}

		}

	}

	@Override
	public BigDecimal valorTotalNoAno() {
		Optional<BigDecimal> optional = Optional.ofNullable(manager
				.createQuery("select sum(valorTotal) From Venda where year(dataCriacao)=:ano and status=:status",
						BigDecimal.class)
				.setParameter("ano", Year.now().getValue()).setParameter("status", StatusVenda.EMITIDA)
				.getSingleResult());

		return optional.orElse(BigDecimal.ZERO);
	}

	@Override
	public BigDecimal valorTotalNoMes() {

		Optional<BigDecimal> optional = Optional.ofNullable(manager
				.createQuery("select sum(valorTotal) From Venda where month(dataCriacao)=:mes and status=:status",
						BigDecimal.class)
				.setParameter("mes", MonthDay.now().getMonthValue()).setParameter("status", StatusVenda.EMITIDA)
				.getSingleResult());

		return optional.orElse(BigDecimal.ZERO);
	}

	@Override
	public BigDecimal valorTicketMedioNoAno() {
		Optional<BigDecimal> optional = Optional.ofNullable(manager
				.createQuery(
						"select sum(valorTotal)/count(*) From Venda where year(dataCriacao)=:ano and status=:status",
						BigDecimal.class)
				.setParameter("ano", Year.now().getValue()).setParameter("status", StatusVenda.EMITIDA)
				.getSingleResult());

		return optional.orElse(BigDecimal.ZERO);

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<VendaMes> totalPorMes() {

		List<VendaMes> vendasMes = manager.createNamedQuery("Vendas.totalPorMes").getResultList();
		LocalDate hoje = LocalDate.now();

		for (int i = 1; i <= 6; i++) {
			String mesIdeal = String.format("%d/%02d", hoje.getYear(), hoje.getMonthValue());
			boolean possuiMes = vendasMes.stream().filter(v -> v.getMes().equals(mesIdeal)).findAny().isPresent();
			if (!possuiMes) {
				vendasMes.add(i - 1, new VendaMes(mesIdeal, 0));
			}
			hoje = hoje.minusMonths(1);
		}

		return vendasMes;
	}

	@Override
	public List<VendaOrigem> totalPorOrigem() {

		List<VendaOrigem> vendasNacionalidade = manager.createNamedQuery("Vendas.porOrigem", VendaOrigem.class)
				.getResultList();

		LocalDate now = LocalDate.now();
		for (int i = 1; i <= 6; i++) {
			String mesIdeal = String.format("%d/%02d", now.getYear(), now.getMonth().getValue());

			boolean possuiMes = vendasNacionalidade.stream().filter(v -> v.getMes().equals(mesIdeal)).findAny()
					.isPresent();
			if (!possuiMes) {
				vendasNacionalidade.add(i - 1, new VendaOrigem(mesIdeal, 0, 0));
			}

			now = now.minusMonths(1);
		}

		return vendasNacionalidade;
	}

}
