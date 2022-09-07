package pdl.backend;

public enum EnumProcessing {

    LUMI(0),
    FLOUMOY(1),
    FLOUGAUSS(2),
    HISTO(3),
    COLOR(4),
    SOBEL(5),
    NEGATIVE(6),
    THRESHOLD(7),
    PIXEL(8),
    SEPIA(9),
    SEPIACONTRASTE(10),
    INVERSECOLOR(11),
    FLAG(12),
    BRUIT(13),
    FUSIONRGB(14),
    PHOTOMATON(15),
    FUSIONHSV(17),
    ENCODE(19);

    EnumProcessing(int code) {
    }
}
