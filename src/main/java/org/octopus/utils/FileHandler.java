
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


import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

public class FileHandler {
    final static int INDEX = 0;

    public static void AppendFiles(ArrayList<Path> paths) throws IOException, NullPointerException {

        FileChannel finalFile;

        finalFile = FileChannel.open(paths.get(INDEX).toAbsolutePath(), StandardOpenOption.APPEND);


        for (int i = 1; i < paths.size(); i++) {

            if (!Files.exists(paths.get(i).toAbsolutePath()))
                throw new IOException("File not exist " + paths.get(i).getFileName());
            FileChannel sourceFile = FileChannel.open(paths.get(i).toAbsolutePath(), StandardOpenOption.READ);

            long size = sourceFile.size();
            long count = 0;
            long position = 0;
            while (position < size) {
                count += sourceFile.transferTo(position, size, finalFile);
                if (count > 0) {
                    position += count;
                    size -= count;
                }


            }
            sourceFile.close();
        }
        finalFile.close();


    }

}
