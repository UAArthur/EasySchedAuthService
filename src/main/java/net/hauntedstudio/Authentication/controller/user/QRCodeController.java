package net.hauntedstudio.Authentication.controller.user;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/auth/")
public class QRCodeController {

    @GetMapping("/qrcode")
    public ResponseEntity<byte[]> generateQRCode() {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();

            // Define custom settings for the QR code
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L); // Low error correction
            hints.put(EncodeHintType.MARGIN, 1); // Default is 4, reducing to 1

            // Generate the BitMatrix with custom settings
            BitMatrix bitMatrix = qrCodeWriter.encode(UUID.randomUUID().toString(), BarcodeFormat.QR_CODE, 128, 128, hints);

            // Convert BitMatrix to BufferedImage
            BufferedImage image = new BufferedImage(128, 128, BufferedImage.TYPE_INT_RGB);
            for (int x = 0; x < 128; x++) {
                for (int y = 0; y < 128; y++) {
                    image.setRGB(x, y, bitMatrix.get(x, y) ? Color.BLACK.getRGB() : Color.WHITE.getRGB());
                }
            }

            // Convert BufferedImage to byte array
            ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
            ImageIO.write(image, "PNG", pngOutputStream);
            byte[] pngData = pngOutputStream.toByteArray();

            // Set headers and return the image as a response
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pngData);

        } catch (WriterException | IOException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
