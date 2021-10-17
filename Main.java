//import javafx.geometry.Point3D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class Main {

    public static class Vertex {
        // vertex object which have an x and y coordinates
        // of the float type
        public float x;
        public float y;
        public Vertex(float x, float y) {
            this.x = x;
            this.y = y;
        }

        // calculates distance between vertices
        static Vertex minus(Vertex p, Vertex q) {
            return new Vertex(p.x-q.x, p.y-q.y);
        }

        // calculate the dot product of two vertices
        static float dot(Vertex p, Vertex q) {
            return p.x*q.x + p.y*q.y;
        }

    }

    public static class Barycentric {
        public float u;
        public float v;
        public float w;

        public float interpolate(float a, float b, float c) {
            return a * u + b * v + w * c;
        }

    }
    public static class SimpleTriangle {
        public int y0 = 20;
        public int y1 = 200;
        public int x0 = 250;
        public int x1 = 250;
        
        // set the vertices of the triangle in which
        // barcyentric coordinates are relatively calculated from
        public Vertex v0 = new Vertex(x0, y0);
        public Vertex v1 = new Vertex(x0 - (y1-y0+1), y1);
        public Vertex v2 = new Vertex(x0 + (y1-y0+1), y1);

        // secondary colors
        // yellow
        public int r0 = 255;
        public int g0 = 255;
        public int b0 = 0;

        // magenta
        public int r1 = 255;
        public int g1 = 0;
        public int b1 = 255;

        // cyan
        public int r2 = 0;
        public int g2 = 255;
        public int b2 = 255;



        void nextScanline() {
            x0--;
            x1++;
        }


        Barycentric CalcBarycentric(Vertex p)
        {
            // barycentric coordinates are calculated based on the positioning
            // of the pixel in relation to the vertices
            Vertex v0 = Vertex.minus(this.v1, this.v0);
            Vertex v1 = Vertex.minus(this.v2, this.v0);
            Vertex v2 = Vertex.minus(p, this.v0);


            float d00 = Vertex.dot(v0, v0);
            float d01 = Vertex.dot(v0, v1);
            float d11 = Vertex.dot(v1, v1);
            float d20 = Vertex.dot(v2, v0);
            float d21 = Vertex.dot(v2, v1);
            float denom = d00 * d11 - d01 * d01;

            Barycentric result = new Barycentric();

            result.v = (d11 * d20 - d01 * d21) / denom;
            result.w = (d00 * d21 - d01 * d20) / denom;
            result.u = 1.0f - result.v - result.w;

            return result;
        }

    }

    public static void main(String[] args) {

        BufferedImage outputImage = new BufferedImage(500,500,BufferedImage.TYPE_INT_RGB);

        SimpleTriangle t = new SimpleTriangle();

        // loop through every pixel in the triangle
        for(int y=t.y0; y<=t.y1; ++y) {
            for(int x=t.x0; x<=t.x1; ++x) {
                Vertex p = new Vertex(x,y);
                Barycentric bary = t.CalcBarycentric(p);
                // linear interpolation calculation
                int r = (int)(bary.interpolate(t.r0, t.r1, t.r2));
                int g = (int)(bary.interpolate(t.g0, t.g1, t.g2));
                int b = (int)(bary.interpolate(t.b0, t.b1, t.b2));

                int color = b | (g<<8) | (r<<16);
                outputImage.setRGB(x,y, color);
            }
            t.nextScanline();
        }



        File output = new File("output.png");
        try {
            ImageIO.write(outputImage, "png", output);
        }
        catch(IOException e) {

        }

        System.out.println("Hello World!");
    }
}