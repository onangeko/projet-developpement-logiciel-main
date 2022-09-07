package pdl.backend;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import boofcv.io.image.ConvertBufferedImage;
import boofcv.struct.image.GrayU8;
import boofcv.struct.image.Planar;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.imageio.ImageIO;

@RestController
public class ImageController {

  @Autowired
  private ObjectMapper mapper;

  private final ImageDao imageDao;

  @Autowired
  public ImageController(ImageDao imageDao) {
    this.imageDao = imageDao;
  }

  @RequestMapping(value = "/images/{id}", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
  public ResponseEntity<?> getImage(@PathVariable("id") long id) {

    Optional<Image> image = imageDao.retrieve(id);

    if (image.isPresent()) {
      InputStream inputStream = new ByteArrayInputStream(image.get().getData());
      return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(new InputStreamResource(inputStream));
    }
    return new ResponseEntity<>("Image id=" + id + " not found.", HttpStatus.NOT_FOUND);
  }

  @RequestMapping(value = "/images/{id}", method = RequestMethod.DELETE)
  public ResponseEntity<?> deleteImage(@PathVariable("id") long id) {

    Optional<Image> image = imageDao.retrieve(id);

    if (image.isPresent()) {
      imageDao.delete(image.get());
      return new ResponseEntity<>("Image id=" + id + " deleted.", HttpStatus.OK);
    }
    return new ResponseEntity<>("Image id=" + id + " not found.", HttpStatus.NOT_FOUND);
  }

  @RequestMapping(value = "/images", method = RequestMethod.POST)
  public ResponseEntity<?> addImage(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {

    String contentType = file.getContentType();
    if (!contentType.equals(MediaType.IMAGE_JPEG.toString()) && !contentType.equals(MediaType.IMAGE_PNG.toString())) {
      return new ResponseEntity<>("Only JPEG file format supported", HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    try {
      imageDao.create(new Image(file.getOriginalFilename(), file.getBytes()));
    } catch (IOException e) {
      return new ResponseEntity<>("Failure to read file", HttpStatus.NO_CONTENT);
    }
    return new ResponseEntity<>("Image uploaded", HttpStatus.CREATED);
  }

  @RequestMapping(value = "/images", method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public ArrayNode getImageList() {
    List<Image> images = imageDao.retrieveAll();
    ArrayNode nodes = mapper.createArrayNode();
    for (Image image : images) {
      ObjectNode objectNode = mapper.createObjectNode();
      objectNode.put("id", image.getId());
      objectNode.put("name", image.getName());
      nodes.add(objectNode);
    }
    return nodes;
  }


  //ajouts
  @RequestMapping(value = "/images/{id}", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE, params = {"algorithm", "p1", "p2"})
  public ResponseEntity<?> getImage(@PathVariable("id") long id, @RequestParam("algorithm") String algo, @RequestParam("p1") Float p1, @RequestParam("p2") Float p2) {
    Optional<Image> image = imageDao.retrieve(id);
    if (image.isEmpty()) {
      return new ResponseEntity<>("Image id=" + id + " not found.", HttpStatus.NOT_FOUND);
    }
    Optional<Image> image2 = Optional.empty();
    float[] option = {p2};
    if (algo.compareTo("flou") == 0) {
      if (p1 == 0) {
        try {
          image2 = imageDao.processing(EnumProcessing.FLOUMOY, image.get(), option);
        } catch (Exception e) {
          return new ResponseEntity<>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
      }
      if (p1 == 1) {
        try {
          image2 = imageDao.processing(EnumProcessing.FLOUGAUSS, image.get(), option);
        } catch (Exception e) {
          return new ResponseEntity<>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
      }
    } else if (algo.compareTo("fusionhsv") == 0) {
      Optional<Image> image3 = imageDao.retrieve(Math.round(p1));
      if (image3.isEmpty()) {
        return new ResponseEntity<>("Image id=" + id + " not found.", HttpStatus.NOT_FOUND);
      }
      try {
        image2 = imageDao.processing2(EnumProcessing.FUSIONHSV, image.get(), image3.get(), option);
      } catch (Exception e) {
        return new ResponseEntity<>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
      }
    } else if (algo.compareTo("fusionrgb") == 0) {
      Optional<Image> image3 = imageDao.retrieve(Math.round(p1));
      if (image3.isEmpty()) {
        return new ResponseEntity<>("Image id=" + id + " not found.", HttpStatus.NOT_FOUND);
      }
      try {
        image2 = imageDao.processing2(EnumProcessing.FUSIONRGB, image.get(), image3.get(), option);
      } catch (Exception e) {
        return new ResponseEntity<>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
      }
    } else {
      return new ResponseEntity<>("Bad Request", HttpStatus.BAD_REQUEST);
    }
    if (image2.isEmpty()) {
      return new ResponseEntity<>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
    }
    InputStream inputStream = new ByteArrayInputStream(image2.get().getData());
    return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(new InputStreamResource(inputStream));

  }
  @RequestMapping(value = "/images/{id}", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE, params = {"algorithm", "p1"})
  public ResponseEntity<?> getImage(@PathVariable("id") long id, @RequestParam("algorithm") String algo, @RequestParam("p1") Float p1) {
    Optional<Image> image = imageDao.retrieve(id);
    if (image.isEmpty()) {
      return new ResponseEntity<>("Image id=" + id + " not found.", HttpStatus.NOT_FOUND);
    }
    Optional<Image> image2;
    float[] option = {p1};
    if (algo.compareTo("color") == 0) {
      try {
        image2 = imageDao.processing(EnumProcessing.COLOR, image.get(), option);
      } catch (Exception e) {
        return new ResponseEntity<>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
      }
    } else if (algo.compareTo("histoEga") == 0) {
      try {
        image2 = imageDao.processing(EnumProcessing.HISTO, image.get(), option);
      } catch (Exception e) {
        return new ResponseEntity<>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
      }
    } else if (algo.compareTo("pixel") == 0) {
      try {
        image2 = imageDao.processing(EnumProcessing.PIXEL, image.get(), option);
      } catch (Exception e) {
        return new ResponseEntity<>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
      }
    } else if (algo.compareTo("increaseLumi") == 0) {
      try {
        image2 = imageDao.processing(EnumProcessing.LUMI, image.get(), option);
      } catch (Exception e) {
        return new ResponseEntity<>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
      }
    } else if (algo.compareTo("bruit") == 0) {
      try {
        image2 = imageDao.processing(EnumProcessing.BRUIT, image.get(), option);
      } catch (Exception e) {
        return new ResponseEntity<>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
      }
    }
    else if (algo.compareTo("threshold") == 0) {
      try {
        image2 = imageDao.processing(EnumProcessing.THRESHOLD, image.get(), option);
      } catch (Exception e) {
        return new ResponseEntity<>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
      }
    } else if (algo.compareTo("flags") == 0) {
      try {
        image2 = imageDao.processing(EnumProcessing.FLAG, image.get(), option);
      } catch (Exception e) {
        return new ResponseEntity<>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
      }
    } else if (algo.compareTo("photomaton") == 0) {
      try {
        image2 = imageDao.processing(EnumProcessing.PHOTOMATON, image.get(), option);
      } catch (Exception e) {
        return new ResponseEntity<>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
      }
    }else if (algo.compareTo("sepiaContraste") == 0) {
      try {
        image2 = imageDao.processing(EnumProcessing.SEPIACONTRASTE, image.get(), option);
      } catch (Exception e) {
        return new ResponseEntity<>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
      }
    } else if (algo.compareTo("anid") == 0) {
      Optional<Image> image3 = imageDao.retrieve(1);
      if (image3.isEmpty()) {
        return new ResponseEntity<>("Image id=" + id + " not found.", HttpStatus.NOT_FOUND);
      }
      try {
        image2 = imageDao.processing2(EnumProcessing.FUSIONRGB, image.get(), image3.get(), option);
      } catch (Exception e) {
        return new ResponseEntity<>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
      }
    } else if (algo.compareTo("photomacron") == 0) {
      Optional<Image> image3 = imageDao.retrieve(0);
      if (image3.isEmpty()) {
        return new ResponseEntity<>("Image id=" + id + " not found.", HttpStatus.NOT_FOUND);
      }
      try {
        image2 = imageDao.processing2(EnumProcessing.FUSIONRGB, image.get(), image3.get(), option);
      } catch (Exception e) {
        return new ResponseEntity<>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
      }
    } else {
      return new ResponseEntity<>("Bad Request", HttpStatus.BAD_REQUEST);
    }
    if (image2.isEmpty()) {
      return new ResponseEntity<>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
    }
    InputStream inputStream = new ByteArrayInputStream(image2.get().getData());
    return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(new InputStreamResource(inputStream));
  }

  @RequestMapping(value = "/images/{id}", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE, params = {"algorithm"})
  public ResponseEntity<?> getImage(@PathVariable("id") long id, @RequestParam("algorithm") String algo) {
    Optional<Image> image = imageDao.retrieve(id);
    if (image.isEmpty()) {
      return new ResponseEntity<>("Image id=" + id + " not found.", HttpStatus.NOT_FOUND);
    }
    Optional<Image> image2;
    float[] option = {};
    if (algo.compareTo("sobel") == 0) {
      try {
        image2 = imageDao.processing(EnumProcessing.SOBEL, image.get(), option);
      } catch (Exception e) {
        return new ResponseEntity<>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
      }
    } else if (algo.compareTo("negative") == 0) {
      try {
        image2 = imageDao.processing(EnumProcessing.NEGATIVE, image.get(), option);
      } catch (Exception e) {
        return new ResponseEntity<>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
      }
    } else if (algo.compareTo("inverseColor") == 0) {
      try {
        image2 = imageDao.processing(EnumProcessing.INVERSECOLOR, image.get(), option);
      } catch (Exception e) {
        return new ResponseEntity<>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
      }
    } else if (algo.compareTo("sepia") == 0) {
      try {
        image2 = imageDao.processing(EnumProcessing.SEPIA, image.get(), option);
      } catch (Exception e) {
        return new ResponseEntity<>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
      }
    }  else {
      return new ResponseEntity<>("Bad Request", HttpStatus.BAD_REQUEST);
    }
    if (image2.isEmpty()) {
      return new ResponseEntity<>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
    }
    InputStream inputStream = new ByteArrayInputStream(image2.get().getData());
    return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(new InputStreamResource(inputStream));
  }

}
