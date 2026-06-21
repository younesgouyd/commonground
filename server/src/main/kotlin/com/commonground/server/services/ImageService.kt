package com.commonground.server.services

import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

@Service
class ImageService {
    private val root = "${System.getProperty("user.home")}/commonground_server/images"

    fun store(file: MultipartFile, folder: String): String {
        if (file.isEmpty) throw IllegalArgumentException("Failed to store empty file.")

        // Create directory if it doesn't exist (e.g., "uploads/profiles")
        val directoryPath = Paths.get(root, folder)
        Files.createDirectories(directoryPath)

        // Generate a unique filename to prevent collisions
        val extension = file.originalFilename?.substringAfterLast('.', "") ?: ""
        val uniqueFilename = "${UUID.randomUUID()}.$extension"
        val destinationPath = directoryPath.resolve(uniqueFilename)

        // Save the file to disk
        file.inputStream.use { inputStream ->
            Files.copy(inputStream, destinationPath)
        }

        // Return the relative URL path to be saved in the DB
        return "/images/$folder/$uniqueFilename"
    }
}