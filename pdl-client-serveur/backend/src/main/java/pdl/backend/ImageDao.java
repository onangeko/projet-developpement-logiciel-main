package pdl.backend;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.nio.file.NoSuchFileException;
import java.util.Objects;
import java.util.Optional;
import boofcv.io.image.ConvertBufferedImage;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.imageio.ImageIO;

import boofcv.struct.image.Planar;
import boofcv.struct.image.GrayU8;

import org.springframework.stereotype.Repository;

@SuppressWarnings("unused")
@Repository
public class ImageDao implements Dao<Image> {

  private final Map<Long, Image> images = new HashMap<>();

  public ImageDao() throws IOException{
    this(Paths.get("../images"));
  }

  public  ImageDao(Path directoryPath)throws IOException {
    recursive_creation(directoryPath);
  }

  public void recursive_creation(Path directoryPath)throws IOException{
    try(DirectoryStream<Path> stream = Files.newDirectoryStream(directoryPath)){
      for (Path filePath: stream) {
        File file = new File(filePath.toString());
        if (file.isDirectory()){
          recursive_creation(filePath);
        }
        else{
          byte[] fileContent;
          fileContent = Files.readAllBytes(filePath);
          Image img = new Image(filePath.getFileName().toString(), fileContent);
          images.put(img.getId(), img);
        }
      }
    }
    catch (IOException e){
      throw new NoSuchFileException("\"images\" folder not found");
    }
  } 

  @Override
  public Optional<Image> retrieve(final long id) {
    return Optional.ofNullable(images.get(id));
  }

  @Override
  public List<Image> retrieveAll() {
    return new ArrayList<Image>(images.values());
  }

  @Override
  public void create(final Image img) {
    images.put(img.getId(), img);
  }

  @Override
  public void update(final Image img, final String[] params) {
    img.setName(Objects.requireNonNull(params[0], "Name cannot be null"));
    images.put(img.getId(), img);
  }

  @Override
  public void delete(final Image img) {
    images.remove(img.getId());
  }

  public Optional<Image> processing(final EnumProcessing algo, final Image img, final float[] options) throws Exception {
    InputStream is = new ByteArrayInputStream(img.getData());
    BufferedImage buffImage = ImageIO.read(is);
    Planar<GrayU8> input = ConvertBufferedImage.convertFromPlanar(buffImage, null, true, GrayU8.class);
    Planar<GrayU8> output = input.createSameShape();
    switch(algo) {
      case LUMI:
        Algos.luminosity(input, output, (int) options[0]);
        break;
      case COLOR:
        Algos.coloringFilter(input, output, options[0]);
        break;
      case FLOUMOY:
        Algos.meanFilter(input, output, (int) options[0]);
        break;
      case FLOUGAUSS:
        Algos.gaussFilter(input, output, (int) options[0]);
        break;
      case HISTO:
        Algos.equalize(input, output, (int) options[0]);
        break;
      case SOBEL:
        Algos.gradientImageSobel(input, output);
        break;
      case NEGATIVE:
        Algos.negative(input, output);
        break;
      case THRESHOLD:
        Algos.threshold(input, output, (int) options[0]);
        break;
      case PIXEL:
        Algos.pixel(input, output, (int) options[0]);
        break;
      case SEPIA:
        Algos.sepia(input, output);
        break;
      case SEPIACONTRASTE:
        Algos.sepiaContraste(input, output, (int) options[0]);
        break;
      case INVERSECOLOR:
        Algos.inverseColor(input, output);
        break;
      case FLAG:
        Algos.flag(input, output, (int) options[0]);
        break;
      case BRUIT:
        Algos.bruit(input, output, (int) options[0]);
        break;
      case PHOTOMATON:
        Algos.photomaton(input, output,(int)options[0] );
        break;
    }
    ConvertBufferedImage.convertTo_U8(output, buffImage, true);
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    ImageIO.write(buffImage, "png", out);
    byte[] outputBytes = out.toByteArray();
    Image outputImage = new Image(img.getName(), outputBytes);
    return Optional.ofNullable(outputImage);//faire transformation du output en image
  }

  public Optional<Image> processing2(final EnumProcessing algo, final Image img,final Image img2, final float[] options) throws Exception {
    InputStream is = new ByteArrayInputStream(img.getData());
    BufferedImage buffImage = ImageIO.read(is);
    Planar<GrayU8> input = ConvertBufferedImage.convertFromPlanar(buffImage, null, true, GrayU8.class);
    InputStream is2 = new ByteArrayInputStream(img2.getData());
    BufferedImage buffImage2 = ImageIO.read(is2);
    Planar<GrayU8> input2 = ConvertBufferedImage.convertFromPlanar(buffImage2, null, true, GrayU8.class);
    Planar<GrayU8> output = input.createSameShape();
    switch(algo){
      case FUSIONHSV:
        Algos.fusionhsv(input, input2, output, (int)options[0]);
        break;
      case FUSIONRGB:
        Algos.fusionrgb(input, input2, output, (int)options[0]);
        break;
    }
    ConvertBufferedImage.convertTo_U8(output, buffImage, true);
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    ImageIO.write(buffImage, "png", out);
    byte[] outputBytes = out.toByteArray();
    Image outputImage = new Image(img.getName(), outputBytes);
    return Optional.ofNullable(outputImage);
  }


}

