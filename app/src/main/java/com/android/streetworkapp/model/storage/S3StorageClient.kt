package com.android.streetworkapp.model.storage

import android.util.Log
import aws.sdk.kotlin.runtime.auth.credentials.StaticCredentialsProvider
import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.model.ListObjectsRequest
import aws.sdk.kotlin.services.s3.model.ObjectCannedAcl
import aws.sdk.kotlin.services.s3.model.PutObjectRequest
import aws.smithy.kotlin.runtime.content.ByteStream
import aws.smithy.kotlin.runtime.net.url.Url
import com.android.sample.BuildConfig

enum class S3Clients(
    val endpoint: String,
    val region: String,
    val accessKey: String,
    val secretKey: String
) {
  DIGITAL_OCEAN(
      endpoint = "https://street-work-app.fra1.digitaloceanspaces.com",
      region = "us-east-1",
      accessKey = BuildConfig.DIGITAL_OCEAN_SPACE_ACCESS_KEY,
      secretKey =
          BuildConfig
              .DIGITAL_OCEAN_SPACE_SECRET_KEY) // note: the region doesn't actually matter for
                                               // digitalocean but we have to specify it
}

class S3StorageClient(
    private val s3Client: S3Client,
    private val endpoint: String
) { // unfortunately didn't find a way to retrieve endpoint from s3client, have to store it in the
    // class

  private val DEBUG_PREFIX = "S3StorageClient:"

  companion object {
    fun getDigitalOceanS3Client(): S3Client {
      val credentials = StaticCredentialsProvider {
        accessKeyId = S3Clients.DIGITAL_OCEAN.accessKey
        secretAccessKey = S3Clients.DIGITAL_OCEAN.secretKey
      }

      val endpoint = Url.parse(S3Clients.DIGITAL_OCEAN.endpoint)
      val s3Client = S3Client {
        forcePathStyle = false
        region = S3Clients.DIGITAL_OCEAN.region
        endpointUrl = endpoint
        credentialsProvider = credentials
      }

      return s3Client
    }
  }

  /**
   * Saves the file to our s3 storage, returns the file path on successful upload. Note: all the
   * files will be set to public access read.
   *
   * @param key The path and name of the file, for ex: path/for/file.txt
   * @param content The content of the file
   */
  suspend fun uploadFile(key: String, content: ByteArray) {
    try {
      val request = PutObjectRequest {
        this.key = key
        this.body = ByteStream.fromBytes(content)
        this.acl = ObjectCannedAcl.PublicRead
      }
      s3Client.putObject(request)
    } catch (e: Exception) {
      Log.d(this.DEBUG_PREFIX, "Error uploading file: ${e.message}")
    }
  }

  /**
   * Returns the URL of all file, since we will just upload images, we will assume all the contents
   * of the folders are images for simplicity.
   */
  suspend fun getFolderImagesUrls(prefix: String): List<String>? {
    try {
      val request = ListObjectsRequest { this.prefix = prefix }

      val res = s3Client.listObjects(request)
      val objects = res.contents ?: emptyList()

      val images = mutableListOf<String>()
      for (obj in objects) {
        val key = obj.key ?: continue
        if (key.endsWith("/")) continue // object is a folder

        images.add(key)
      }
      return images.map { "${this.endpoint}/${it}" }
    } catch (e: Exception) {
      println("Error uploading file: ${e.message}")
      return null
    }
  }
}
