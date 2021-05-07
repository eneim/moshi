package com.squareup.moshi.adapters;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import java.io.IOException;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

// Ref: http://www.departments.bucknell.edu/biology/resources/msw3/browse.asp?id=12100786
public class MultiLevelPolymorphicJsonAdapterFactoryTest {

  @Test
  public void fromJson() throws IOException {
    Moshi moshi =
        new Moshi.Builder()
            .add(
                PolymorphicJsonAdapterFactory.of(Hominidae.class, "genus")
                    .withSubtype(Pan.class, "Pan")
                    .withSubtype(Homo.class, "Homo")
            )
            .add(
                PolymorphicJsonAdapterFactory.of(Pan.class, "species")
                    .withSubtype(Paniscus.class, "Paniscus")
                    .withSubtype(Troglodytes.class, "Troglodytes")
            )
            .build();
    JsonAdapter<Hominidae> adapter = moshi.adapter(Hominidae.class);

    assertThat(adapter.fromJson("{\"genus\":\"Pan\", \"species\":\"Paniscus\"}"))
        .isEqualTo(new Paniscus());
  }

  @Test
  public void toJson() {
    Moshi moshi =
        new Moshi.Builder()
            .add(
                PolymorphicJsonAdapterFactory.of(Hominidae.class, "genus")
                    .withSubtype(Pan.class, "Pan")
                    .withSubtype(Homo.class, "Homo")
            )
            .add(
                PolymorphicJsonAdapterFactory.of(Pan.class, "species")
                    .withSubtype(Paniscus.class, "Paniscus")
                    .withSubtype(Troglodytes.class, "Troglodytes")
            )
            .build();
    JsonAdapter<Hominidae> adapter = moshi.adapter(Hominidae.class);

    assertThat(adapter.toJson(new Troglodytes()))
        .isEqualTo("{\"genus\":\"Pan\",\"species\":\"Troglodytes\"}");
  }

  abstract static class Hominidae {
    @NotNull final String genus;

    protected Hominidae(@NotNull String genus) {
      this.genus = genus;
    }

    @Override public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof Hominidae)) return false;
      Hominidae hominidae = (Hominidae) o;
      return genus.equals(hominidae.genus);
    }

    @Override public int hashCode() {
      return genus.hashCode();
    }
  }

  // Classified by `GENUS`
  static class Pan extends Hominidae {
    @NotNull final String species;

    Pan(@NotNull String species) {
      super("Pan");
      this.species = species;
    }

    @Override public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof Pan)) return false;
      if (!super.equals(o)) return false;
      Pan pan = (Pan) o;
      return species.equals(pan.species);
    }

    @Override public int hashCode() {
      int result = super.hashCode();
      result = 31 * result + species.hashCode();
      return result;
    }
  }

  // Classified by `GENUS`
  static class Homo extends Hominidae {
    Homo() {
      super("Homo");
    }
  }

  // Classified by `SPECIES`
  static class Paniscus extends Pan {
    Paniscus() {
      super("Paniscus");
    }
  }

  // Classified by `SPECIES`
  static class Troglodytes extends Pan {
    Troglodytes() {
      super("Troglodytes");
    }
  }
}
