package pdl.backend;

import boofcv.alg.filter.blur.GBlurImageOps;
import boofcv.struct.image.GrayU8;
import boofcv.struct.image.Planar;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;


public class Algos {

    //THRESHOLD & THRESHOLD RGB V
    public static void thresholdNB(GrayU8 input,GrayU8 output, int t) {
        for (int y = 0; y < input.height; ++y) {
            for (int x = 0; x < input.width; ++x) {
                int gl = input.get(x, y);
                if (gl < t) {
                    gl = 0;
                } else {
                    gl = 255;
                }
                output.set(x, y, gl);
            }
        }
    }
    public static void threshold(Planar<GrayU8> input,Planar<GrayU8> output,int t){
        for (int i = 0; i<input.getNumBands();i++){
            thresholdNB(input.getBand(i),output.getBand(i),t);
        }
    }

    //LUMI && LUMIRGB V
    public static void luminosityNB(GrayU8 input,GrayU8 output, int delta) {
        for (int y = 0 ; y < input.height; y++) {
            for (int x = 0; x < input.width; x++) {
                if (input.get(x,y) + delta > 255){
                    output.set(x, y,255);
                }else output.set(x, y, Math.max(input.get(x, y) + delta, 0));
            }
        }
    }
    public static void luminosity(Planar<GrayU8> input,Planar<GrayU8> output, int delta){
        for (int i=0;i< input.getNumBands();i++){
            luminosityNB(input.getBand(i),output.getBand(i),delta);
        }
    }

    /// CONVOLUTION V
    public static void convolution(GrayU8 input, GrayU8 output, int[][] kernel) {
        int kernelFactor = kernelFactor(kernel);
        for (int y = ((kernel.length - 1) / 2) ; y <=input.getHeight()-((kernel.length+1)/2);++y){
            for (int x = ((kernel[0].length - 1) / 2) ; x <= input.getWidth()-((kernel[0].length+1)/2);++x){
                int val = 0;
                for (int u = -((kernel.length - 1)/2); u <= ((kernel.length - 1) / 2); ++u){
                    for (int v = -((kernel[0].length - 1)/2); v <= ((kernel[0].length - 1) / 2); ++v){
                        val += input.get(x + u, y + v) * kernel[u+((kernel.length - 1)/2)][v+((kernel.length - 1)/2)];
                    }
                }
                output.set(x,y,(val)/kernelFactor);
            }
        }
    }
    public static int kernelFactor (int [][]tab){
        int cpt = 0;
        for (int[] ints : tab) {
            for (int j = 0; j < tab[0].length; j++) {
                cpt += Math.abs(ints[j]);
            }
        }
        return cpt;
    }

    /// MOYENNEUR & MOYENNEUR RGB V
    public static void meanFilterNB(GrayU8 input, GrayU8 output, int size){
        for (int y = ((size - 1) / 2) ; y <= input.getHeight()-((size+1)/2);++y){
            for (int x = ((size - 1) / 2); x <= input.getWidth()-((size+1)/2);++x){
                int val = 0;
                for (int u = -((size - 1)/2); u <= ((size - 1) / 2); u++){
                    for (int v = -((size - 1)/2); v <= ((size - 1) / 2); v++){
                        val += input.get(x + u, y + v);
                    }
                }
                output.set(x,y,val/(size*size));
            }
        }
    }
    public static void meanFilter(Planar<GrayU8> input, Planar<GrayU8> output, int size){
        for (int i = 0; i <input.getNumBands();i++){
            meanFilterNB(input.getBand(i),output.getBand(i),size);
        }
    }

    // TOGRAYSCALE V
    public static void grayscale(Planar<GrayU8> input, Planar<GrayU8> output){
        for (int y = 0; y < input.getHeight(); ++y) {
            for (int x = 0; x < input.getWidth(); ++x) {
                int pixelValue;
                int red = input.getBand(0).get(x,y);
                int green = input.getBand(1).get(x,y);
                int blue = input.getBand(2).get(x,y);
                pixelValue = Math.round((red * 0.3f) + (green * 0.59f) + (blue * 0.11f));
                for (int i = 0 ;i<input.getNumBands();i++){
                    output.getBand(i).set(x,y, pixelValue);
                }
            }
        }
    }

    //HISTO EGA V
    public static void equalize(Planar<GrayU8> input, Planar<GrayU8> output,int canal){
        int[] histo = new int[256];
        float[] hsv = new float[3];
        int[] rgb = new int[3];
        for (int y = 0 ; y < input.getHeight() ; y++) {
            for (int x = 0; x < input.getWidth(); x++) {
                for(int i = 0; i < input.getNumBands(); i++){
                    rgb[i] = input.getBand(i).get(x,y);
                }
                rgbToHsv(rgb[0], rgb[1], rgb[2], hsv);
                histo[(int)(hsv[canal] * 255)]++;
            }
        }
        float[] hisLut = histoLut(histo,input.totalPixels());
        for (int y = 0 ; y < input.getHeight() ; y++) {
            for (int x = 0; x < input.getWidth(); x++) {
                for(int i = 0; i < input.getNumBands(); i++){
                    rgb[i] = input.getBand(i).get(x,y);
                }
                rgbToHsv(rgb[0], rgb[1], rgb[2], hsv);
                hsv[canal] = hisLut[(int)(hsv[canal] * 255)];

                hsvToRgb(hsv[0], hsv[1], hsv[2], rgb);
                for (int i = 0 ; i < input.getNumBands(); i++) {
                    output.getBand(i).set(x, y, rgb[i]);
                }
            }
        }  
    }
    public static float[] histoLut(int[] his,int nbPix){
        int[] cumul = new int[256];
        cumul[0]=his[0];
        for(int i=1;i<256;i++){
            cumul[i]=cumul[i-1]+his[i];   
        }   

        float[] lut = new float[256];

        for(int i = 0; i < 256; i++){
		    lut[i] = (float) cumul[i]/nbPix;
            if(lut[i] > 1) lut[i] = 1;
			if(lut[i] < 0) lut[i] = 0;
		}
        return lut;
    }  

    //RGB TO HSV V
    static int min(int r,int g,int b){
        int min=r;
        if (g<min){
         min=g;
        }
        if (b<min){
            min=b;
        }
        return min;
    }
    static int max(int r,int g,int b){
        int max=r;
        if (g>max){
            max=g;
        }
        if (b>max){
            max=b;
        }
        return max;
    }
    static void rgbToHsv(int r, int g, int b, float[] hsv){
		float min=min(r,g,b);
		float max=max(r,g,b);
		float fr=(float)(r);
		float fg=(float)(g);
		float fb=(float)(b);
		if (max ==min){
			hsv[0]=0;
		}
		else if (max==fr){
			hsv[0]=(((fg-fb)*60/(max-min))+360)%360;
		}
		else if (max==fg){
			hsv[0]=((fb-fr)*60/(max-min))+120;
		}
		else if (max==fb){
			hsv[0]=((fr-fg)*60/(max-min))+240;	
		}

		if (max==0){
			hsv[1]=0;
		}else{
			hsv[1]=1-(min/max);
		}
		hsv[2]= max/(float)255;
	}

    // HSV TO RGB V
	static void hsvToRgb(float h, float s, float v, int[] rgb){
        v=v*255;
		int ti=((int)(h/60))%6;
		float f=(h/60)-ti;
		int l=(int)(v*(1-s));
		int m=(int)(v*(1-s*f));
		int n=(int)(v*(1-((1-f)*s)));
		if (ti==0){
			rgb[0]=(int)v;
			rgb[1]=n;
			rgb[2]=l;
		}
		if (ti==1){
			rgb[0]=m;
			rgb[1]=(int)v;
			rgb[2]=l;
		}
		if (ti==2){
			rgb[0]=l;
			rgb[1]=(int)v;
			rgb[2]=n;
		}
		if (ti==3){
			rgb[0]=l;
			rgb[1]=m;
			rgb[2]=(int)v;
		}
		if (ti==4){
			rgb[0]=n;
			rgb[1]=l;
			rgb[2]=(int)v;
		}
		if (ti==5){
			rgb[0]=(int)v;
			rgb[1]=l;
			rgb[2]=m;
			
		}
	}

    // COLORING FILTER V
    public static void coloringFilter(Planar<GrayU8>input,Planar<GrayU8>output,float delta){
        while(delta<0)delta+=360;
        for(int y=0;y<input.height;y++){
            for(int x=0;x<input.width;x++){
                int[] rgb=new int[3];
                for(int i=0;i<input.getNumBands();i++){
                    rgb[i]=input.getBand(i).get(x,y); 
                } 
                float[] hsv=new float[3];
                rgbToHsv(rgb[0] , rgb[1], rgb[2], hsv);
                hsvToRgb(delta%360 , hsv[1], hsv[2], rgb);
                for(int i=0;i<input.getNumBands();i++){
                    output.getBand(i).set(x,y,rgb[i]); 
                } 
            } 
        }      
    } 

    // inverse couleur
    public static void inverseColor(Planar<GrayU8>input,Planar<GrayU8>output){
        for(int y=0;y<input.height;y++){
            for(int x=0;x<input.width;x++){
                int[] rgb=new int[3];
                for(int i=0;i<input.getNumBands();i++){
                    rgb[i]=input.getBand(i).get(x,y); 
                } 
                float[] hsv=new float[3];
                rgbToHsv(rgb[0] , rgb[1], rgb[2], hsv);
                
                hsvToRgb((hsv[0]+180)%360  , hsv[1], hsv[2], rgb);
                for(int i=0;i<input.getNumBands();i++){
                    output.getBand(i).set(x,y,rgb[i]); 
                } 
            } 
        }      
    } 
    // SOBEL V
    public static void gradientImageSobelNB(GrayU8 input, GrayU8 output) {

        int[][] sobelX = {
                {-1, 0, 1},
                {-2, 0, 2},
                {-1, 0, 1}
        };

        int[][] sobelY = {
                {-1, -2, -1},
                {0, 0, 0},
                {1, 2, 1}
        };
        for (int y = (sobelY.length - 1) / 2; y <= (input.getHeight() - ((sobelY.length + 1) / 2)); ++y){
            for (int x = ((sobelX[0].length - 1) / 2) ; x <= input.getWidth()-((sobelX[0].length+1)/2);++x){
                int valx = 0;
                int valy = 0;
                for (int u = -((sobelX.length - 1)/2); u <= ((sobelX.length - 1) / 2); ++u){
                    for (int v = -((sobelX[0].length - 1)/2); v <= ((sobelX[0].length - 1) / 2); ++v){
                        valx += sobelX[u+((sobelX.length - 1)/2)][v+((sobelX.length - 1)/2)] * input.get(x + u, y + v);
                        valy += sobelY[u+((sobelY.length - 1)/2)][v+((sobelY.length - 1)/2)] * input.get(x + u, y + v) ;
                        if (valx>255){
                            valx = 255;
                        }else if (valy > 255){
                            valy = 255;
                        }
                    }
                }
                output.set(x,y,(int) Math.hypot(valx,valy));
            }
        }
    }

    //SOBEL RGB V
    public static void gradientImageSobel(Planar<GrayU8> input, Planar<GrayU8> output) {
        Planar<GrayU8> gray=input.clone();
        grayscale(input, gray);
        for(int i=0;i<input.getNumBands();i++){
            gradientImageSobelNB(gray.getBand(i),output.getBand(i));
        }
    }

    //GAUSS NB & GAUSS RGB V
    public static void gaussFilterNB(GrayU8 input, GrayU8 output){
        int [][] GaussKernel = {
                {1,2,3,2,1},
                {2,6,8,6,2},
                {3,8,10,8,3},
                {2,6,8,6,2},
                {1,2,3,2,1},};
        convolution(input,output,GaussKernel);
    }
    public static void gaussFilter(Planar<GrayU8> image, Planar<GrayU8> output, int option){
        GBlurImageOps.gaussian(image, output, -1, option, null);
    }
    public static void gaussFilter2(Planar<GrayU8> image, Planar<GrayU8> output, int option){
        for (int i=0;i< image.getNumBands();i++){
            gaussFilterNB(image.getBand(i),output.getBand(i));
        }
    }

    // NEGATIVE V
    public static void negative(Planar<GrayU8>input,Planar<GrayU8>output){
        int pixelValue;
        for(int y=0;y<input.height;y++){
            for(int x=0;x<input.width;x++) {
                for (int i = 0 ;i<input.getNumBands();i++){
                    int bandValue = input.getBand(i).get(x,y);
                    pixelValue = (255-bandValue);
                    output.getBand(i).set(x,y, pixelValue);
                }
            }
        }
    }
    // PIXEL V
    public static void pixel(Planar<GrayU8>input,Planar<GrayU8>output,int level){
        for(int y=0;y<input.height;y+=level){
            for(int x=0;x<input.width;x+=level) {
                for (int i = 0 ;i<input.getNumBands();i++){
                    int moy=moy(input.getBand(i),level,x,y);
                    applymoy(output.getBand(i),level,x,y,moy);
                }
            }
        }
    }
    public static int moy(GrayU8 input,int level, int x,int y){
        int tot=0;
        for(int i=y;i<y+level && i<input.height;i++){
            for(int j=x;j<x+level && j<input.width;j++){
                tot+=input.get(j,i);
            }
        }
        return tot/(level*level);
    }
    public static void applymoy(GrayU8 input,int level, int x,int y,int moy){
        for(int i=y;i<y+level && i<input.height;i++){
            for(int j=x;j<x+level && j<input.width;j++){
                input.set(j,i,moy);
            }
        }
    }

    // SEPIA V
    public static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
    public static void sepia(Planar<GrayU8>input,Planar<GrayU8>output) {
        for (int y = 0; y < input.height; y++) {
            for (int x = 0; x < input.width; x++) {
                int red = input.getBand(0).get(x, y);
                int green = input.getBand(1).get(x, y);
                int blue = input.getBand(2).get(x, y);

                int newRed = clamp((int) (0.393 * red + 0.769 * green + 0.189 * blue), 0, 255);
                int newGreen = clamp((int) (0.349 * red + 0.686 * green + 0.168 * blue), 0, 225);
                int newBlue = clamp((int) (0.272 * red + 0.534 * green + 0.131 * blue), 0, 255);

                output.getBand(0).set(x, y, newRed);
                output.getBand(1).set(x, y, newGreen);
                output.getBand(2).set(x, y, newBlue);
            }
        }
    }
    public static void sepiaContraste(Planar<GrayU8> input,Planar<GrayU8>output,int threshold){ // SEPIA CONTRASTE COULEUR
        for(int y=0;y<output.height;y++) {
            for (int x = 0; x < output.width; x++) {
                int pixelValue;
                int red = input.getBand(0).get(x,y);
                int green = input.getBand(1).get(x,y);
                int blue = input.getBand(2).get(x,y);
                pixelValue = ((red + green + blue))/3;
                if (pixelValue <= threshold) {
                    int k = pixelValue/threshold;
                    int rs = 94*k;
                    int gs = 38*k;
                    int bs = 38*k;
                    output.getBand(0).set(x,y, rs );
                    output.getBand(1).set(x,y, gs );
                    output.getBand(2).set(x,y, bs );
                }else {
                    int k = (pixelValue - threshold) / (255 - threshold);
                    output.getBand(0).set(x, y, clamp(  94 + (k * (255 - 94)),0,255));
                    output.getBand(1).set(x, y, clamp(  38 + (k * (255 - 38)),0,255));
                    output.getBand(2).set(x, y, clamp(  18 + (k * (255 - 18 )),0,255));
                }
            }
        }
    }

    // FLAGS V

    public static void frenchFlag (Planar<GrayU8> input, Planar<GrayU8> output){
        int range = (input.getWidth()/3);
        for (int y = 0; y < input.getHeight() ; ++y) {
            for (int x = 0; x <range ; ++x) {
                output.getBand(2).set(x, y,(input.getBand(2).get(x,y)+165)/2);
            }
        }
        for (int y = 0;y<input.height; y++) {
            for (int x = range+1; x < 2 * range; x++) {
                for (int i = 0; i < input.getNumBands();i++) {
                    if (input.getBand(i).get(x,y) + 80 > 255){
                        output.getBand(i).set(x, y,255);
                    }else output.getBand(i).set(x, y, Math.max(input.getBand(i).get(x, y) + 80, 0));
                }
            }
        }
        for (int y = 0;y<input.height; y++) {
            for (int x = 2*range + 1; x< input.width; x++) {
                output.getBand(0).set(x, y,(input.getBand(0).get(x,y)+239)/2);
            }
        }

    }
    public static void germanFlag(Planar<GrayU8> input, Planar<GrayU8> output){
        int range = (input.getHeight()/3);
        for (int y = 0;y<range; y++) {
            for (int x = 0; x < input.getWidth(); x++) {
                for (int i = 0; i < input.getNumBands();i++) {
                    if (input.getBand(i).get(x, y) - 50 > 255){
                        output.getBand(i).set(x, y,255);
                    }else output.getBand(i).set(x, y, Math.max(input.getBand(i).get(x, y) - 50, 0));
                }
            }
        }
        for (int y = range+1;y<2*range; y++) {
            for (int x = 0; x< input.width; x++) {
                output.getBand(0).set(x, y,(input.getBand(0).get(x,y)+221)/2);
            }
        }
        for (int y = 2*range;y<input.getHeight(); y++) {
            for (int x = 0; x< input.width; x++) {
                output.getBand(0).set(x, y,(input.getBand(0).get(x,y)+255)/2);
                output.getBand(1).set(x,y,(input.getBand(1).get(x,y)+206)/2);
            }
        }


    }
    public static void gabonFlag(Planar<GrayU8> input, Planar<GrayU8> output){
        int range = (input.getWidth()/3);
        for (int y = 0; y < input.getHeight() ; ++y) {
            for (int x = 0; x <range ; ++x) {
                output.getBand(1).set(x, y,(input.getBand(1).get(x,y)+158)/2);
                output.getBand(2).set(x, y,(input.getBand(2).get(x,y)+96)/2);
            }
        }
        for (int y = 0; y < input.getHeight() ; ++y) {
            for (int x = range+1; x <2*range ; ++x) {
                output.getBand(0).set(x, y,(input.getBand(0).get(x,y)+252)/2);
                output.getBand(1).set(x, y,(input.getBand(1).get(x,y)+209)/2);
                output.getBand(2).set(x, y,(input.getBand(2).get(x,y)+22)/2);
            }
        }
        for (int y = 0;y<input.getHeight(); y++) {
            for (int x = 2*range+1; x< input.width; x++) {
                output.getBand(0).set(x, y,(input.getBand(0).get(x,y)+58)/2);
                output.getBand(1).set(x,y,(input.getBand(1).get(x,y)+117)/2);
                output.getBand(2).set(x, y,(input.getBand(2).get(x,y)+196)/2);
            }
        }

    }


    public static void flag(Planar<GrayU8> input,Planar<GrayU8>output,int c) {
        switch (c){
            case 0:
                frenchFlag(input,output);
                break;
            case 1:
                germanFlag(input,output);
                break;
            case 2:
                gabonFlag(input,output);
                break;
        }

    }


    /// STENO X ///

    public static String stenoDecode (Planar<GrayU8> input, int band) {
        int b;
        int i = 0;
        int z = 8;
        byte[] tab = new byte[((input.getWidth()*input.getHeight())/8)+1];

        for (int x = 0 ; x < input.getWidth() ; x++){
            for (int y = 0 ; y < input.getHeight() ; y++){
                tab[i] += ((input.getBand(band).get(x, y))%2)*Math.pow(2, z);
                z--;
                if (z == 0){
                    z = 8;
                    i++;
                }
            }
        }

        return decToAscii(tab);
    }

    public static void stenoEncode (Planar<GrayU8> input, Planar<GrayU8> output, String message,int band) {//fait vite donc a verifier
        for(int y=0;y<input.height;y++){
            for(int x=0;x<input.width;x++){
                for(int i=0;i<input.getNumBands();i++){
                    output.getBand(i).set(x,y,input.getBand(i).get(x,y));
                }
            }
        }
        byte[] b=message.getBytes(StandardCharsets.US_ASCII);
        Integer[] i=decToBin(b);
        int byte_traite=0;
        while (byte_traite<b.length){
            for (int x = 0 ; x < input.getWidth() ; x++){
                for (int y = 0 ; y < input.getHeight() ; y++){
                    int value=input.getBand(band).get(x, y);
                    if (value==255 && i[byte_traite]==1){
                        value -= 2;
                    }
                    value+=i[byte_traite];
                    output.getBand(band).set(x,y,value);
                    byte_traite++;
                }
            }
        }
    }

    public static Integer[] decToBin(byte[] b) {
        ArrayList<Integer> list=new ArrayList<>();
        for (byte value : b) {
            int[] a = new int[8];
            int actual_int = value;
            for (int j = 8; j < 0; j--) {
                list.add(actual_int % (int) (Math.pow(2, j)));
                actual_int -= (actual_int % (int) (Math.pow(2, j))) * (int) (Math.pow(2, j));
            }
        }
        return list.toArray(new Integer[0]);
    }

    public static String decToAscii(byte[] dec)
    {
        StringBuilder msg = new StringBuilder();

        for (byte b : dec) {
            msg.append((char) b);
        }

        return msg.toString();
    }

    /// BRUIT V ///


    public static void bruit(Planar<GrayU8>input,Planar<GrayU8>output,int plage){
        for(int y=0;y<input.height;y++){
            for(int x=0;x<input.width;x++){
                for(int i=0;i<input.getNumBands();i++){
                    int value=input.getBand(i).get(x,y); 
                    value+=(Math.random()*plage)-(plage/2);
                    if (value<0)value=0;
                    else if (value>255)value=255;
                    output.getBand(i).set(x,y,value);
                } 
            } 
        }      
    } 

    public static void fusionhsv(Planar<GrayU8>input1,Planar<GrayU8>input2,Planar<GrayU8>output,int pourcentage){
        for(int y=0;y<input1.height;y++){
            for(int x=0;x<input1.width;x++){
                for(int i=0;i<input1.getNumBands();i++){
                    output.getBand(i).set(x,y,input1.getBand(i).get(x,y));
                }
            }
        }
        if (pourcentage>100) pourcentage=100;
        else if (pourcentage<0) pourcentage=0;
        int difx=input1.width-input2.width;
        int dify=input1.height-input2.height; 
        if (difx>=0){
            for(int x=0;x<input2.width;x++){
                if (dify>=0){
                    for(int y=0;y<input2.height;y++){
                        int[] rgb1=new int[3];
                        int[] rgb2=new int[3];
                        for(int i=0;i<input1.getNumBands();i++){
                            rgb1[i]=input1.getBand(i).get(x+(difx/2),y+(dify/2)); 
                            rgb2[i]=input2.getBand(i).get(x,y); 
                        } 
                        float[] hsv1=new float[3];
                        float[] hsv2=new float[3];
                        rgbToHsv(rgb1[0] , rgb1[1], rgb1[2], hsv1);
                        rgbToHsv(rgb2[0] , rgb2[1], rgb2[2], hsv2);
                        if (hsv1[1]==0||hsv1[2]==0){//test de gestion de cas particulier, atester avec le front
                            hsvToRgb(hsv2[0] ,(hsv1[1]*(100-pourcentage)+hsv2[1]*pourcentage)/100, (hsv1[2]*(100-pourcentage)+hsv2[2]*pourcentage)/100, rgb1);
                        } 
                        else if (hsv2[1]==0||hsv2[2]==0){
                            hsvToRgb(hsv1[0] ,(hsv1[1]*(100-pourcentage)+hsv2[1]*pourcentage)/100, (hsv1[2]*(100-pourcentage)+hsv2[2]*pourcentage)/100, rgb1);
                        } else{
                            hsvToRgb((hsv1[0]*(100-pourcentage)+hsv2[0]*pourcentage)/100 ,(hsv1[1]*(100-pourcentage)+hsv2[1]*pourcentage)/100, (hsv1[2]*(100-pourcentage)+hsv2[2]*pourcentage)/100, rgb1);
                        } 
                        for(int i=0;i<output.getNumBands();i++){
                            output.getBand(i).set(x+(difx/2),y+(dify/2),rgb1[i]); 
                        } 
                    }
                } 

                else {
                    for(int y=0;y<input1.height;y++){
                        int[] rgb1=new int[3];
                        int[] rgb2=new int[3];
                        for(int i=0;i<input1.getNumBands();i++){
                            rgb1[i]=input1.getBand(i).get(x+(difx/2),y); 
                            rgb2[i]=input2.getBand(i).get(x,y-(dify/2)); 
                        } 
                        float[] hsv1=new float[3];
                        float[] hsv2=new float[3];
                        rgbToHsv(rgb1[0] , rgb1[1], rgb1[2], hsv1);
                        rgbToHsv(rgb2[0] , rgb2[1], rgb2[2], hsv2);
                        if (hsv1[1]==0||hsv1[2]==0){//test de gestion de cas particulier, atester avec le front
                            hsvToRgb(hsv2[0] ,(hsv1[1]*(100-pourcentage)+hsv2[1]*pourcentage)/100, (hsv1[2]*(100-pourcentage)+hsv2[2]*pourcentage)/100, rgb1);
                        } 
                        else if (hsv2[1]==0||hsv2[2]==0){
                            hsvToRgb(hsv1[0] ,(hsv1[1]*(100-pourcentage)+hsv2[1]*pourcentage)/100, (hsv1[2]*(100-pourcentage)+hsv2[2]*pourcentage)/100, rgb1);
                        } else{
                            hsvToRgb((hsv1[0]*(100-pourcentage)+hsv2[0]*pourcentage)/100 ,(hsv1[1]*(100-pourcentage)+hsv2[1]*pourcentage)/100, (hsv1[2]*(100-pourcentage)+hsv2[2]*pourcentage)/100, rgb1);
                        } 
                        for(int i=0;i<output.getNumBands();i++){
                            output.getBand(i).set(x+(difx/2),y,rgb1[i]); 
                        } 
                    }
                } 
            } 
        } 
        else {
            for(int x=0;x<input1.width;x++){
                if (dify>=0){
                    for(int y=0;y<input2.height;y++){
                        int[] rgb1=new int[3];
                        int[] rgb2=new int[3];
                        for(int i=0;i<input1.getNumBands();i++){
                            rgb1[i]=input1.getBand(i).get(x,y+(dify/2)); 
                            rgb2[i]=input2.getBand(i).get(x-(difx/2),y); 
                        } 
                        float[] hsv1=new float[3];
                        float[] hsv2=new float[3];
                        rgbToHsv(rgb1[0] , rgb1[1], rgb1[2], hsv1);
                        rgbToHsv(rgb2[0] , rgb2[1], rgb2[2], hsv2);
                        if (hsv1[1]==0||hsv1[2]==0){//test de gestion de cas particulier, atester avec le front
                            hsvToRgb(hsv2[0] ,(hsv1[1]*(100-pourcentage)+hsv2[1]*pourcentage)/100, (hsv1[2]*(100-pourcentage)+hsv2[2]*pourcentage)/100, rgb1);
                        } 
                        else if (hsv2[1]==0||hsv2[2]==0){
                            hsvToRgb(hsv1[0] ,(hsv1[1]*(100-pourcentage)+hsv2[1]*pourcentage)/100, (hsv1[2]*(100-pourcentage)+hsv2[2]*pourcentage)/100, rgb1);
                        } else{
                            hsvToRgb((hsv1[0]*(100-pourcentage)+hsv2[0]*pourcentage)/100 ,(hsv1[1]*(100-pourcentage)+hsv2[1]*pourcentage)/100, (hsv1[2]*(100-pourcentage)+hsv2[2]*pourcentage)/100, rgb1);
                        } 
                        for(int i=0;i<output.getNumBands();i++){
                            output.getBand(i).set(x,y+(dify/2),rgb1[i]); 
                        } 
                    }
                } 

                else {
                    for(int y=0;y<input1.height;y++){
                        int[] rgb1=new int[3];
                        int[] rgb2=new int[3];
                        for(int i=0;i<input1.getNumBands();i++){
                            rgb1[i]=input1.getBand(i).get(x,y); 
                            rgb2[i]=input2.getBand(i).get(x-(difx/2),y-(dify/2)); 
                        } 
                        float[] hsv1=new float[3];
                        float[] hsv2=new float[3];
                        rgbToHsv(rgb1[0] , rgb1[1], rgb1[2], hsv1);
                        rgbToHsv(rgb2[0] , rgb2[1], rgb2[2], hsv2);
                        if (hsv1[1]==0||hsv1[2]==0){//test de gestion de cas particulier, atester avec le front
                            hsvToRgb(hsv2[0] ,(hsv1[1]*(100-pourcentage)+hsv2[1]*pourcentage)/100, (hsv1[2]*(100-pourcentage)+hsv2[2]*pourcentage)/100, rgb1);
                        } 
                        else if (hsv2[1]==0||hsv2[2]==0){
                            hsvToRgb(hsv1[0] ,(hsv1[1]*(100-pourcentage)+hsv2[1]*pourcentage)/100, (hsv1[2]*(100-pourcentage)+hsv2[2]*pourcentage)/100, rgb1);
                        } else{
                            hsvToRgb((hsv1[0]*(100-pourcentage)+hsv2[0]*pourcentage)/100 ,(hsv1[1]*(100-pourcentage)+hsv2[1]*pourcentage)/100, (hsv1[2]*(100-pourcentage)+hsv2[2]*pourcentage)/100, rgb1);
                        } 
                        for(int i=0;i<output.getNumBands();i++){
                            output.getBand(i).set(x,y,rgb1[i]); 
                        } 
                    }
                } 
            } 
        }
    }

    public static void fusionrgb(Planar<GrayU8>input1,Planar<GrayU8>input2,Planar<GrayU8>output,int pourcentage){
        for(int y=0;y<input1.height;y++){
            for(int x=0;x<input1.width;x++){
                for(int i=0;i<input1.getNumBands();i++){
                    output.getBand(i).set(x,y,input1.getBand(i).get(x,y));
                }
            }
        }
        if (pourcentage>100) pourcentage=100;
        else if (pourcentage<0) pourcentage=0;
        int difx=input1.width-input2.width;
        int dify=input1.height-input2.height; 
        if (difx>=0){
            for(int x=0;x<input2.width;x++){
                if (dify>=0){
                    for(int y=0;y<input2.height;y++){
                        for(int i=0;i<input1.getNumBands();i++){
                            int a=((input1.getBand(i).get(x+(difx/2),y+(dify/2))*(100-pourcentage))+(input2.getBand(i).get(x,y)*pourcentage))/100 ;
                            if (a<0)a=0;
                            else if (a>255)a=255;
                            output.getBand(i).set(x+(difx/2),y+(dify/2),a); 
                        } 
                    }
                } 

                else {
                    for(int y=0;y<input1.height;y++){
                        for(int i=0;i<input1.getNumBands();i++){
                            int a =(input1.getBand(i).get(x+(difx/2),y)*(100-pourcentage)+(input2.getBand(i).get(x,y-(dify/2))*pourcentage))/100; 
                            if (a<0)a=0;
                            else if (a>255)a=255;

                            output.getBand(i).set(x+(difx/2),y,a);
                        } 
                    }
                } 
            } 
        } 
        else {
            for(int x=0;x<input1.width;x++){
                if (dify>=0){
                    for(int y=0;y<input2.height;y++){
                        for(int i=0;i<input1.getNumBands();i++){
                            int a =((input1.getBand(i).get(x,y+(dify/2))*(100-pourcentage))+(input2.getBand(i).get(x-(difx/2),y))*(pourcentage))/100; 
                            if (a<0)a=0;
                            else if (a>255)a=255;
                            output.getBand(i).set(x,y+(dify/2),a); 
                        } 
                    }
                } 

                else {
                    for(int y=0;y<input1.height;y++){
                        for(int i=0;i<input1.getNumBands();i++){
                            int a=((input1.getBand(i).get(x,y)*(100-pourcentage))+(input2.getBand(i).get(x-(difx/2),y-(dify/2))*(pourcentage)))/100; 
                            if (a<0)a=0;
                            else if (a>255)a=255;
                            output.getBand(i).set(x,y,a); 
                        } 
                    }
                } 
            } 
        }
    }

    public static void photomaton(Planar<GrayU8>input,Planar<GrayU8>output,int times){
        

        Planar<GrayU8> tmp1 = input.createSameShape();
        Planar<GrayU8> tmp2 = input.createSameShape();
        for(int y=0;y<input.height;y++){
            for(int x=0;x<input.width;x++){
                for(int i=0;i<input.getNumBands();i++){
                    tmp1.getBand(i).set(x,y,input.getBand(i).get(x,y));
                    tmp2.getBand(i).set(x,y,input.getBand(i).get(x,y));
                }
            }
        }
        for (int a =0;a<times;a++){
            for(int y=0;y<tmp1.height-tmp1.height%2;y++){
                for(int x=0;x<tmp1.width-tmp1.width%2;x++){
                    if (y%2==0 && x%2==0){
                        for(int i=0;i<tmp1.getNumBands();i++){
                            tmp2.getBand(i).set(x/2,y/2,tmp1.getBand(i).get(x,y));
                        }
                    }
                    else if (y%2==1 && x%2==0){
                        for(int i=0;i<tmp1.getNumBands();i++){
                            tmp2.getBand(i).set(x/2,(y/2)+(tmp1.height/2),tmp1.getBand(i).get(x,y));
                        }
                    } 
                    else if (y % 2 == 0){
                        for(int i=0;i<tmp1.getNumBands();i++){
                            tmp2.getBand(i).set((x/2)+(tmp1.width/2),y/2,tmp1.getBand(i).get(x,y));
                        }
                    } 
                    else {
                        for(int i=0;i<tmp1.getNumBands();i++){
                            tmp2.getBand(i).set((x/2)+(tmp1.width/2),(y/2)+(tmp1.height/2),tmp1.getBand(i).get(x,y));
                        }
                    } 
                } 
            } 
            for(int y=0;y<tmp2.height;y++){
                for(int x=0;x<tmp2.width;x++){
                    for(int i=0;i<tmp2.getNumBands();i++){
                        tmp1.getBand(i).set(x,y,tmp2.getBand(i).get(x,y));
                    }
                }
            }
        }
        for(int y=0;y<tmp2.height;y++){
            for(int x=0;x<tmp2.width;x++){
                for(int i=0;i<tmp2.getNumBands();i++){
                    output.getBand(i).set(x,y,tmp2.getBand(i).get(x,y));
                }
            }
        }   
    }    
}


















