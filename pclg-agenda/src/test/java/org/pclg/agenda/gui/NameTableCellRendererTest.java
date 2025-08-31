//package org.pclg.agenda.gui;
//
//import com.google.common.jimfs.Configuration;
//import com.google.common.jimfs.Jimfs;
//import org.testng.annotations.Test;
//
//import javax.imageio.ImageIO;
//import java.awt.image.BufferedImage;
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.nio.file.FileSystem;
//import java.nio.file.Files;
//import java.nio.file.Path;
//
public class NameTableCellRendererTest {
//
//    @Test
//    public void testGetTableCellRendererComponent() throws IOException {
//        FileSystem fs = Jimfs.newFileSystem(Configuration.unix());
//        Path dir = fs.getPath("/foo");
//        Files.createDirectory(dir);
//
//        String s = "/home/pablo/development/projects/Alles/pclg-apps/src/main/html/resources/adium.png";
//        final BufferedImage bufferedImage = ImageIO.read(new File(s));
//
//        if (bufferedImage != null) {
////            final ImageIcon thumbnail = bufferedImage.get();
////
////            BufferedImage image = new BufferedImage(
////                thumbnail.getIconWidth(),
////                thumbnail.getIconHeight(),
////                BufferedImage.TYPE_INT_RGB);
////            Graphics g = image.createGraphics();
////// paint the Icon to the BufferedImage.
////            thumbnail.paintIcon(null, g, 0,0);
////            g.dispose();
//
//            final String imagePath = "xx.jpg";
//            Path hello = dir.resolve(imagePath);
//            final File file = Files.createFile(hello).toFile();
////            final File file = hello.toFile();
////            ImageIO.write(bufferedImage, "JPG", new FileOutputStream(file));
//            ImageIO.write(bufferedImage, "JPG", file);
//
//            final var absolutePath = file.getAbsolutePath();
//            System.out.println("absolutePath = " + absolutePath + " exists = " + file.exists());
//        }
//        }
}