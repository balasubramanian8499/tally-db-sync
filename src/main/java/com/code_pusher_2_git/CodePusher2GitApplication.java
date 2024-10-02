package com.code_pusher_2_git;

import okhttp3.*;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

@SpringBootApplication
public class CodePusher2GitApplication {

	private static final String GITHUB_API_URL = "https://api.github.com";
	private static final String OWNER = "your username";
	private static final String REPO_NAME = "repo name";
	private static final String PAT = "your git personal access token";

	public static void main(String[] args) {
		String localFilePath = "your path";
		OkHttpClient client = new OkHttpClient();
		createRepository(client);
		addFileToRepository(client, localFilePath);
	}

	private static void createRepository(OkHttpClient client) {
		MediaType mediaType = MediaType.parse("application/json");
		RequestBody body = RequestBody.create("{\"name\": \"" + REPO_NAME + "\"}", mediaType);
		Request request = new Request.Builder()
				.url(GITHUB_API_URL + "/user/repos")
				.post(body)
				.addHeader("Authorization", "Bearer " + PAT)
				.build();
		try (Response response = client.newCall(request).execute()) {
			if (response.isSuccessful()) {
				System.out.println("Repository created successfully.");
			} else {
				assert response.body() != null : "Response body is null";
				System.out.println("Failed to create repository: " + response.body().string());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void addFileToRepository(OkHttpClient client, String localFilePath) {
		try {
			File directory = new File(localFilePath);
			if (directory.isDirectory()) {
				Files.walk(Paths.get(localFilePath))
						.filter(Files::isRegularFile)
						.forEach(file -> {
							try {
								byte[] fileContent = Files.readAllBytes(file);
								String encodedContent = Base64.getEncoder().encodeToString(fileContent);
								String filePath = directory.toPath().relativize(file).toString().replace("\\", "/");
								String commitMessage = "Adding " + filePath;
								String branchName = "main"; // or "master" depending on your repository settings

								MediaType mediaType = MediaType.parse("application/json");
								String json = "{\n" +
										"  \"message\": \"" + commitMessage + "\",\n" +
										"  \"content\": \"" + encodedContent + "\",\n" +
										"  \"branch\": \"" + branchName + "\"\n" +
										"}";
								RequestBody body = RequestBody.create(json,mediaType);
								Request request = new Request.Builder()
										.url(GITHUB_API_URL + "/repos/" + OWNER + "/" + REPO_NAME + "/contents/" + filePath)
										.put(body)
										.addHeader("Authorization", "Bearer " + PAT)
										.build();
								try (Response response = client.newCall(request).execute()) {
									if (response.isSuccessful()) {
										System.out.println("File " + filePath + " added successfully.");
									} else {
										assert response.body() != null : "Response body is null";
										System.out.println("Failed to add file " + filePath + ": " + response.body().string());
									}
								}
							} catch (IOException e) {
								e.printStackTrace();
							}
						});
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
