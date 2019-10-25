package com.yt8492

import io.ktor.util.KtorExperimentalAPI
import jdk.nashorn.internal.runtime.regexp.joni.Config.log
import software.amazon.awssdk.auth.credentials.AwsCredentials
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.BucketCannedACL
import software.amazon.awssdk.services.s3.model.ObjectCannedACL
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.io.File
import java.net.URI
import java.util.*

@KtorExperimentalAPI
object ImageFactory {

    private const val bucketName = "sample"
    private val s3 = S3Client.builder()
        .endpointOverride(AppConfig.s3Url.let(URI::create))
        .region(Region.US_EAST_1)
        .credentialsProvider {
            object : AwsCredentials {
                override fun accessKeyId(): String = AppConfig.awsAccessKeyId

                override fun secretAccessKey(): String = AppConfig.awsSecretAccessKey
            }
        }
        .build()

    fun putImage(file: File): String {
        s3.createBucket {
            it.bucket(bucketName)
                .acl(BucketCannedACL.PUBLIC_READ)
                .build()
        }

        val name = UUID.randomUUID().toString().replace("-", "")
        val ext = file.extension
        val key = "images/$name.$ext"
        val req = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .acl(ObjectCannedACL.PUBLIC_READ)
            .build()
        s3.putObject(req, RequestBody.fromFile(file))
        return key
    }

    fun getImage(key: String): File {
        val res = s3.getObject {
            it.bucket(bucketName)
                .key(key)
                .build()
        }
        val file = File.createTempFile("hogehoge", "fugafuga")
        res.buffered().use { input ->
            file.outputStream().buffered().use { output ->
                input.copyTo(output)
            }
        }
        return file
    }
}