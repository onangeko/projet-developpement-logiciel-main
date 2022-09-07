package pdl.backend;

public class Image {
  private static Long count = 0L;
  private final Long id;
  private String name;
  private final byte[] data;

  public Image(final String name, final byte[] data) {
    id = count++;
    this.name = name;
    this.data = data;
  }

  public long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public byte[] getData() {
    return data;
  }
}
