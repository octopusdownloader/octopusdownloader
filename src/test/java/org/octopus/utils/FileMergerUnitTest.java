/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2018 by octopusdownloader
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.octopus.utils;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import org.junit.*;
import org.junit.rules.TemporaryFolder;
import org.junit.runners.MethodSorters;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FileMergerUnitTest {
    @ClassRule
    public static TemporaryFolder tmpFolder = new TemporaryFolder();
    private static ArrayList<Path> paths;
    private static HashCode checksum;
    private static Path image;

    @BeforeClass
    public static void initializing() {
        paths = new ArrayList<>();
        image = Paths.get("src", "test", "resources", "image.jpg");

        FileOutputStream outputStream = null;
        FileInputStream inputStream = null;
        BufferedInputStream bufferedInputStream = null;

        try {
            checksum = com.google.common.io.Files.asByteSource(new File(image.toString())).hash(Hashing.md5());
            byte[] imageByte = Files.readAllBytes(image);
            byte[] buffer = new byte[imageByte.length / 4]; //creating 4 parts

            File fr = new File(String.valueOf(image));
            inputStream = new FileInputStream(fr);
            bufferedInputStream = new BufferedInputStream(inputStream);
            int byteRead = 0, i = 0;

            while ((byteRead = bufferedInputStream.read(buffer)) > 0) {
                Path newPath = Paths.get(tmpFolder.getRoot().getCanonicalPath(), String.valueOf(i++) + ".part");
                paths.add(newPath);
                File newFile = new File(newPath.toString());
                outputStream = new FileOutputStream(newFile);
                outputStream.write(buffer, 0, byteRead);
                outputStream.close();
            }
            inputStream.close();
        } catch (IOException e) {
            e.getMessage();
            e.printStackTrace();
        }
    }

    @AfterClass
    public static void remove() {
        tmpFolder.delete();
    }

    @Test()
    public void stage1_shouldAppendFilesTest() {
        try {
            FileMerger.AppendFiles(paths);
            HashCode appendFilesCode = com.google.common.io.Files.asByteSource(paths.get(0).toFile()).hash(Hashing.md5());
            assertEquals(checksum, appendFilesCode);
        } catch (IOException e) {
            e.getMessage();
        }
    }

    @Test(expected = IOException.class)
    public void stage2_shouldnotAppendWhenFilesNotExist() throws IOException {
        paths.add(Paths.get(tmpFolder.getRoot().getCanonicalPath(), "5.part"));
        FileMerger.AppendFiles(paths);
    }

    @Test
    public void stage3_shouldnotAppendasMD5isDifferent() throws IOException {

        //getting last part added
        Path path = paths.get(5);
        File fd = new File(path.toAbsolutePath().toString());
        FileOutputStream outputStream = new FileOutputStream(fd);
        byte[] bytes = Files.readAllBytes(image);
        outputStream.write(bytes);
        outputStream.flush();
        outputStream.close();
        FileMerger.AppendFiles(paths);
        HashCode appendFilesCode = com.google.common.io.Files.asByteSource(paths.get(0).toFile()).hash(Hashing.md5());
        assertNotEquals(appendFilesCode, checksum);
    }
}