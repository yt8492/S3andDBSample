package com.yt8492

import com.yt8492.model.GetImageRequest
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import io.ktor.request.receive
import io.ktor.request.receiveMultipart
import io.ktor.response.respond
import io.ktor.response.respondFile
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import io.ktor.util.KtorExperimentalAPI
import java.io.File

@KtorExperimentalAPI
fun Route.putImage() {
    route("image") {
        post {
            val multipart = call.receiveMultipart()
            multipart.forEachPart { part ->
                when (part) {
                    is PartData.FileItem -> {
                        val fileName = part.originalFileName ?: return@forEachPart
                        val file = File(fileName)
                        part.streamProvider().buffered().use { input ->
                            file.outputStream().buffered().use { output ->
                                input.copyTo(output)
                            }
                        }
                        ImageFactory.putImage(file)
                    }
                }
                part.dispose()
            }
            call.respond(HttpStatusCode.Accepted)
        }

        get {
            val request = call.receive<GetImageRequest>()
            val file = ImageFactory.getImage(request.key)
            call.respondFile(file)
        }
    }
}