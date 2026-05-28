package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.AvatarRepository;
import ru.hogwarts.school.repository.StudentRepository;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.nio.file.StandardOpenOption.CREATE_NEW;

@Service
public class AvatarService {
    private static final Logger logger = LoggerFactory.getLogger(AvatarService.class);
    @Value("${path.to.avatars.folder}")
    private String avatarsDir;
    private final AvatarRepository avatarRepository;
    private final StudentRepository studentRepository;

    public AvatarService(AvatarRepository avatarRepository, StudentRepository studentRepository) {
        this.avatarRepository = avatarRepository;
        this.studentRepository = studentRepository;
    }

    public Avatar findAvatar(long studentId) {
        logger.info("Was invoked method for find avatar by student id");
        logger.debug("Searching avatar for student with id: {}", studentId);
        Avatar avatar = avatarRepository.findByStudentId(studentId).orElse(new Avatar());
        logger.debug("Found avatar with id: {} for student id: {}", avatar.getId(), studentId);
        return avatar;
    }

    public Avatar uploadAvatar(long studentId, MultipartFile avatarFile) throws IOException {
        logger.info("Was invoked method for upload avatar");
        logger.debug("Uploading avatar for student id: {}, file name: {}, size: {} bytes",
                studentId, avatarFile.getOriginalFilename(), avatarFile.getSize());
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> {
                    logger.error("Student not found with ID: {}", studentId);
                    return new RuntimeException("Student not found with ID: " + studentId);
                });
        Path filePath = Path.of(avatarsDir, studentId + "." + getExtension(avatarFile.getOriginalFilename()));
        Files.createDirectories(filePath.getParent());
        Files.deleteIfExists(filePath);
        logger.debug("Saving avatar file to path: {}", filePath);
        try (
                InputStream is = avatarFile.getInputStream();
                OutputStream os = Files.newOutputStream(filePath, CREATE_NEW);
                BufferedInputStream bis = new BufferedInputStream(is, 1024);
                BufferedOutputStream bos = new BufferedOutputStream(os, 1024)
        ) {
            bis.transferTo(bos);
        }
        Avatar avatar = findAvatar(studentId);
        avatar.setStudent(student);
        avatar.setFilePath(filePath.toString());
        avatar.setFileSize(avatarFile.getSize());
        avatar.setMediaType(avatarFile.getContentType());
        avatar.setData(generateDataForDb(filePath));
        Avatar savedAvatar = avatarRepository.save(avatar);
        logger.debug("Avatar successfully uploaded and saved with id: {}", savedAvatar.getId());
        return savedAvatar;
    }

    private byte[] generateDataForDb(Path filePath) throws IOException {
        logger.info("Was invoked method for generate data for DB");
        logger.debug("Generating thumbnail data for file: {}", filePath);
        try (InputStream is = Files.newInputStream(filePath);
             BufferedInputStream bis = new BufferedInputStream(is, 1024);
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            BufferedImage image = ImageIO.read(bis);
            if (image == null) {
                logger.warn("Unable to read image from file: {}. File may not be a valid image.", filePath);
                throw new IOException("Invalid image file");
            }

            int height = image.getHeight() / (image.getWidth() / 100);
            BufferedImage preview = new BufferedImage(100, height, image.getType());
            Graphics2D graphics = preview.createGraphics();
            graphics.drawImage(image, 0, 0, 100, height, null);
            graphics.dispose();
            ImageIO.write(preview, getExtension(filePath.getFileName().toString()), bos);
            logger.debug("Successfully generated thumbnail data for file: {}, size: {} bytes", filePath, bos.size());
            return bos.toByteArray();
        }
    }

    private String getExtension(String fileName) {
        logger.debug("Extracting extension from file name: {}", fileName);
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1);
        logger.debug("Extracted extension: {}", extension);
        return extension;
    }
}