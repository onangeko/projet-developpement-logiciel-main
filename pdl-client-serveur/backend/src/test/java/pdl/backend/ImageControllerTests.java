package pdl.backend;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.file.Files;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(OrderAnnotation.class)
public class ImageControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @BeforeAll
    public static void reset() {
        // reset Image class static counter
        ReflectionTestUtils.setField(Image.class, "count", 0L);
    }

    @Test
    @Order(1)
    public void getImageListShouldReturnSuccess() throws Exception {
        this.mockMvc.perform(get("/images")).andExpect(status().isOk());
    }

    @Test
    @Order(2)
    public void getImageShouldReturnNotFound() throws Exception {
        /*
         * On admet qu'on ne mettra pas plus de 5000 images dans notre galerie, et que
         * le test ne trouvera jamais la 5000e image
         */
        this.mockMvc.perform(get("/images/5000")).andExpect(status().isNotFound());
    }

    @Test
    @Order(3)
    public void getImageShouldReturnSuccess() throws Exception {
        /* On considère qu'il y a toujours une image à l'indice 0 dans notre galerie */
        this.mockMvc.perform(get("/images/0")).andExpect(status().isOk());
    }

    @Test
    @Order(4)
    public void deleteImagesShouldReturnMethodNotAllowed() throws Exception {
        /* Le delete n'agit pas sur un URL contenant seulement /images */
        this.mockMvc.perform(delete("/images")).andExpect(status().isMethodNotAllowed());
    }

    @Test
    @Order(5)
    public void deleteImageShouldReturnNotFound() throws Exception {
        /* Comme on a pas plus de 5000 images, la 5001e ne devrait pas exister */
        this.mockMvc.perform(delete("/images/5001")).andExpect(status().isNotFound());
    }

    @Test
    @Order(6)
    public void deleteImageShouldReturnSuccess() throws Exception {
        /* On admet encore une fois que l'image 0 existe et qu'on peut la supprimer */
        this.mockMvc.perform(delete("/images/0")).andExpect(status().isOk());
    }

    @Test
    @Order(7)
    public void createImageShouldReturnSuccess() throws Exception {
        final ClassPathResource cpr = new ClassPathResource("test.jpg");
        MockMultipartFile mmf = new MockMultipartFile("file", "test.jpg", "image/jpeg",
                Files.readAllBytes(cpr.getFile().toPath()));
        this.mockMvc.perform(MockMvcRequestBuilders.multipart("/images").file(mmf)).andExpect(status().isOk());
    }

    @Test
    @Order(8)
    public void createImageShouldReturnUnsupportedMediaType() throws Exception {
        /* Le type xcx n'est pas supporté et renverra une erreur */
        final ClassPathResource cpr = new ClassPathResource("test.jpg");
        MockMultipartFile mmf = new MockMultipartFile("file", "test.jpg", "image/xcx",
                Files.readAllBytes(cpr.getFile().toPath()));
        this.mockMvc.perform(MockMvcRequestBuilders.multipart("/images").file(mmf))
                .andExpect(status().isUnsupportedMediaType());
    }

    //2-ARGS

    @Test
    @Order(9)
    public void flouShouldReturnSuccess() throws Exception {
        this.mockMvc.perform(get("/images/0?algorithm=flou&p1=1&p2=20")).andExpect(status().isOk());
    }

    @Test
    @Order(10)
    public void flouShouldReturnNotFound() throws Exception {
        this.mockMvc.perform(get("/images/5000?algorithm=flou&p1=1&p2=20")).andExpect(status().isNotFound());
    }

    @Test
    @Order(11)
    public void flouShouldReturnMethodNotAllowed() throws Exception {
        ///
    }

    @Test
    @Order(12)
    public void flouShouldReturnBadRequest() throws Exception {
        this.mockMvc.perform(get("/images/5000?algorithm=flou&p1=a&p2=20")).andExpect(status().isBadRequest());
    }

    @Test
    @Order(13)
    public void flouShouldReturnInternalServerError() throws Exception {
        ///
    }

    //1-ARG

    @Test
    @Order(14)
    public void colorShouldReturnSuccess() throws Exception {
        this.mockMvc.perform(get("/images/0?algorithm=color&p1=20")).andExpect(status().isOk());
    }

    @Test
    @Order(15)
    public void colorShouldReturnNotFound() throws Exception {
        this.mockMvc.perform(get("/images/5000?algorithm=color&p1=20")).andExpect(status().isNotFound());
    }

    @Test
    @Order(16)
    public void colorShouldReturnMethodNotAllowed() throws Exception {
        ///
    }

    @Test
    @Order(17)
    public void colorShouldReturnBadRequest() throws Exception {
        this.mockMvc.perform(get("/images/5000?algorithm=color&p1=a")).andExpect(status().isBadRequest());
    }

    @Test
    @Order(18)
    public void colorShouldReturnInternalServerError() throws Exception {
        ///
    }

    @Test
    @Order(19)
    public void histoEgaShouldReturnSuccess() throws Exception {
        this.mockMvc.perform(get("/images/0?algorithm=histoEga&p1=2")).andExpect(status().isOk());
    }

    @Test
    @Order(20)
    public void histoEgaShouldReturnNotFound() throws Exception {
        this.mockMvc.perform(get("/images/5000?algorithm=histoEga&p1=2")).andExpect(status().isNotFound());
    }

    @Test
    @Order(21)
    public void histoEgaShouldReturnMethodNotAllowed() throws Exception {
        ///
    }

    @Test
    @Order(22)
    public void histoEgaShouldReturnBadRequest() throws Exception {
        this.mockMvc.perform(get("/images/5000?algorithm=histoEga&p1=a")).andExpect(status().isBadRequest());
    }

    @Test
    @Order(23)
    public void histoEgaShouldReturnInternalServerError() throws Exception {
        ///
    }

    @Test
    @Order(24)
    public void pixelShouldReturnSuccess() throws Exception {
        this.mockMvc.perform(get("/images/0?algorithm=pixel&p1=3")).andExpect(status().isOk());
    }

    @Test
    @Order(25)
    public void pixelShouldReturnNotFound() throws Exception {
        this.mockMvc.perform(get("/images/5000?algorithm=pixel&p1=3")).andExpect(status().isNotFound());
    }

    @Test
    @Order(26)
    public void pixelShouldReturnMethodNotAllowed() throws Exception {
        ///
    }

    @Test
    @Order(27)
    public void pixelShouldReturnBadRequest() throws Exception {
        this.mockMvc.perform(get("/images/5000?algorithm=pixel&p1=a")).andExpect(status().isBadRequest());
    }

    @Test
    @Order(28)
    public void pixelShouldReturnInternalServerError() throws Exception {
        ////
    }

    @Test
    @Order(29)
    public void increaseLumiShouldReturnSuccess() throws Exception {
        this.mockMvc.perform(get("/images/0?algorithm=increaseLumi&p1=30")).andExpect(status().isOk());
    }

    @Test
    @Order(30)
    public void increaseLumiShouldReturnNotFound() throws Exception {
        this.mockMvc.perform(get("/images/5000?algorithm=increaseLumi&p1=30")).andExpect(status().isNotFound());
    }

    @Test
    @Order(31)
    public void increaseLumiShouldReturnMethodNotAllowed() throws Exception {
        ///
    }

    @Test
    @Order(32)
    public void increaseLumiShouldReturnBadRequest() throws Exception {
        this.mockMvc.perform(get("/images/5000?algorithm=increaseLumi&p1=a")).andExpect(status().isBadRequest());
    }

    @Test
    @Order(33)
    public void increaseLumiShouldReturnInternalServerError() throws Exception {
        ///
    }

    @Test
    @Order(34)
    public void thresholdShouldReturnSuccess() throws Exception {
        this.mockMvc.perform(get("/images/0?algorithm=threshold&p1=30")).andExpect(status().isOk());
    }

    @Test
    @Order(35)
    public void thresholdShouldReturnNotFound() throws Exception {
        this.mockMvc.perform(get("/images/5000?algorithm=threshold&p1=30")).andExpect(status().isNotFound());
    }

    @Test
    @Order(36)
    public void thresholdShouldReturnMethodNotAllowed() throws Exception {
        ///
    }

    @Test
    @Order(37)
    public void thresholdShouldReturnBadRequest() throws Exception {
        this.mockMvc.perform(get("/images/5000?algorithm=threshold&p1=a")).andExpect(status().isBadRequest());
    }

    @Test
    @Order(38)
    public void thresholdShouldReturnInternalServerError() throws Exception {
        ///
    }

    ///NO-ARGS

    @Test
    @Order(39)
    public void sobelShouldReturnSuccess() throws Exception {
        this.mockMvc.perform(get("/images/0?algorithm=sobel")).andExpect(status().isOk());
    }

    @Test
    @Order(40)
    public void sobelShouldReturnNotFound() throws Exception {
        this.mockMvc.perform(get("/images/5000?algorithm=sobel")).andExpect(status().isNotFound());
    }

    @Test
    @Order(41)
    public void sobelShouldReturnMethodNotAllowed() throws Exception {
        ///
    }

    @Test
    @Order(42)
    public void sobelShouldReturnBadRequest() throws Exception {
        this.mockMvc.perform(get("/images/5000?algorithm=sobel&p1=a")).andExpect(status().isBadRequest());
    }

    @Test
    @Order(43)
    public void sobelShouldReturnInternalServerError() throws Exception {
        ///
    }

    @Test
    @Order(44)
    public void negativeShouldReturnSuccess() throws Exception {
        this.mockMvc.perform(get("/images/0?algorithm=negative")).andExpect(status().isOk());
    }

    @Test
    @Order(45)
    public void negativeShouldReturnNotFound() throws Exception {
        this.mockMvc.perform(get("/images/5000?algorithm=negative")).andExpect(status().isNotFound());
    }

    @Test
    @Order(46)
    public void negativeShouldReturnMethodNotAllowed() throws Exception {
        ///
    }

    @Test
    @Order(47)
    public void negativeShouldReturnBadRequest() throws Exception {
        this.mockMvc.perform(get("/images/5000?algorithm=negative&p1=a")).andExpect(status().isBadRequest());
    }

    @Test
    @Order(48)
    public void negativeShouldReturnInternalServerError() throws Exception {
        ///
    }

    @Test
    @Order(49)
    public void sepiaShouldReturnSuccess() throws Exception {
        this.mockMvc.perform(get("/images/0?algorithm=sepia")).andExpect(status().isOk());
    }

    @Test
    @Order(50)
    public void sepiaShouldReturnNotFound() throws Exception {
        this.mockMvc.perform(get("/images/5000?algorithm=sepia")).andExpect(status().isNotFound());
    }

    @Test
    @Order(51)
    public void sepiaShouldReturnMethodNotAllowed() throws Exception {
        ///
    }

    @Test
    @Order(52)
    public void sepiaShouldReturnBadRequest() throws Exception {
        this.mockMvc.perform(get("/images/5000?algorithm=sepia&p1=a")).andExpect(status().isBadRequest());
    }

    @Test
    @Order(53)
    public void sepiaShouldReturnInternalServerError() throws Exception {
        ///
    }

    @Test
    @Order(54)
    public void inverseColorShouldReturnSuccess() throws Exception {
        this.mockMvc.perform(get("/images/0?algorithm=inverseColor")).andExpect(status().isOk());
    }

    @Test
    @Order(55)
    public void inverseColorShouldReturnNotFound() throws Exception {
        this.mockMvc.perform(get("/images/5000?algorithm=inverseColor")).andExpect(status().isNotFound());
    }

    @Test
    @Order(56)
    public void inverseColorShouldReturnMethodNotAllowed() throws Exception {
        ///
    }

    @Test
    @Order(57)
    public void inverseColorShouldReturnBadRequest() throws Exception {
        this.mockMvc.perform(get("/images/5000?algorithm=inverseColor&p1=a")).andExpect(status().isBadRequest());
    }

    @Test
    @Order(58)
    public void inverseColorShouldReturnInternalServerError() throws Exception {
        ///
    }
}