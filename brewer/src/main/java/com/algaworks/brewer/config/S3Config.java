package com.algaworks.brewer.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

@Profile("prod")
@Configuration
@PropertySource(value = {"file://${HOME}/.brewer-s3.properties"}, ignoreResourceNotFound = true)
public class S3Config {
	
	@Autowired
	private Environment env;
	
	@Bean
	public AmazonS3 amazonS3() {
		AWSCredentials credenciais = new BasicAWSCredentials(
				env.getProperty("AWS_ACCESS_KEY_ID"), env.getProperty("AWS_ACCESS_KEY_PASSWORD"));
		//AmazonS3 s3 = new AmazonS3Client(credenciais, new ClientConfiguration());		
//		
//		Region region = Region.getRegion(Regions.US_EAST_1);
//		s3.setRegion(region);
		
		/*
		AmazonS3 s3Client = new AmazonS3Client(credentials, new ClientConfiguration());
		s3Client.setS3ClientOptions(S3ClientOptions.builder().setPathStyleAccess(true).build());
		s3Client.setEndpoint("http://localhost:9444/s3");*/
		
		AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                //.withRegion(Regions.US_EAST_1)
                .withCredentials(new AWSStaticCredentialsProvider(credenciais))
                //.withClientConfiguration(new ClientConfiguration())
                .withEndpointConfiguration(new EndpointConfiguration("http://localhost:9444/brewer", "us-west-1"))
                .build();
		
		
		
		
		return s3Client;
		
	} 

}
