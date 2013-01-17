/*
 * Copyright (C) 2000-2013 Heinz Max Kabutz
 *
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.  Heinz Max Kabutz licenses
 * this file to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.javaspecialists.tjsn.jmatia.actions;

import javax.imageio.*;
import javax.imageio.stream.*;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;

public class ScreenShot implements RobotAction {
    // this is used on the student JVM to optimize transfers
    private static final ThreadLocal<byte[]> previous =
            new ThreadLocal<>();
    private static final float JPG_QUALITY = 0.3f;

    private final double scale;

    public ScreenShot(double scale) {
        this.scale = scale;
    }

    public Object execute(Robot robot) throws IOException {
        long time = System.currentTimeMillis();
        Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
        Rectangle shotArea = new Rectangle(
                defaultToolkit.getScreenSize());
        BufferedImage image = robot.createScreenCapture(shotArea);
        if (scale != 1.0) {
            image = getScaledInstance(image);
        }
        byte[] bytes = convertToJPG(image);
        time = System.currentTimeMillis() - time;
        System.out.println("time = " + time);
        // only send it if the picture has actually changed
        byte[] prev = previous.get();
        if (prev != null && Arrays.equals(bytes, prev)) {
            return null;
        }
        previous.set(bytes);
        return bytes;
    }

    private byte[] convertToJPG(BufferedImage img)
            throws IOException {
        ImageWriter writer =
                ImageIO.getImageWritersByFormatName("jpg").next();
        ImageWriteParam iwp = writer.getDefaultWriteParam();
        iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        iwp.setCompressionQuality(JPG_QUALITY);

        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        writer.setOutput(new MemoryCacheImageOutputStream(bout));
        writer.write(null, new IIOImage(img, null, null), iwp);
        writer.dispose();
        bout.flush();
        return bout.toByteArray();
    }

    public BufferedImage getScaledInstance(BufferedImage src) {
        int width = (int) (src.getWidth() * scale);
        int height = (int) (src.getHeight() * scale);

        Image scaled = src.getScaledInstance(width, height,
                BufferedImage.SCALE_AREA_AVERAGING);
        BufferedImage result = new BufferedImage(
                width, height, BufferedImage.TYPE_INT_RGB
        );
        result.createGraphics().drawImage(
                scaled, 0, 0, width, height, null);
        return result;
    }

    public String toString() {
        return "ScreenShot(" + scale + ")";
    }

    public boolean equals(Object o) {
        if (!(o instanceof ScreenShot)) return false;
        return Double.compare(((ScreenShot) o).scale, scale) == 0;
    }

    public int hashCode() {
        long temp = Double.doubleToLongBits(scale);
        return (int) (temp ^ (temp >>> 32));
    }
}
