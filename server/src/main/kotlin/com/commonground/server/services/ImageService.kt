package com.commonground.server.services

import com.commonground.core.models.Base64Image
import com.commonground.core.models.ImageUrl
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import kotlin.io.encoding.Base64

@Service
class ImageService {
    private val root = "${System.getProperty("user.home")}/commonground_server/images"

    fun store(value: Base64Image, folder: String): String {
        val directoryPath = Paths.get(root, folder)
        Files.createDirectories(directoryPath)

        val uniqueFilename = UUID.randomUUID().toString()
        val destinationPath = directoryPath.resolve(uniqueFilename)

        Base64.decode(value).inputStream().use { inputStream ->
            Files.copy(inputStream, destinationPath)
        }
        return "/images/$folder/$uniqueFilename"
    }

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

    fun delete(relativeUrl: ImageUrl): Boolean {
        // Extract the relative folder/filename path by stripping the "/images/" prefix
        val relativePath = relativeUrl.removePrefix("/images/")
        val filePath = Paths.get(root, relativePath).normalize()

        // Guard against path traversal attacks (e.g., matching outside the root directory)
        require(filePath.startsWith(Paths.get(root).normalize())) { "Invalid file path navigation attempt." }

        return Files.deleteIfExists(filePath)
    }
}