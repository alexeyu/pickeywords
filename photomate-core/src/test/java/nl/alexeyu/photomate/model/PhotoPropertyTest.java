package nl.alexeyu.photomate.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class PhotoPropertyTest {

    @Test
    public void hasProperty() {
        assertTrue(PhotoProperty.has("keywords"));
        assertFalse(PhotoProperty.has("editor"));
        assertFalse(PhotoProperty.has(null));
    }

    @Test
    public void ofExistingProperty() {
        assertSame(PhotoProperty.of("creator"), PhotoProperty.CREATOR);
    }

    @Test(expected = IllegalArgumentException.class)
    public void ofNonExistingProperty() {
        PhotoProperty.of("date");
    }

    @Test(expected = IllegalArgumentException.class)
    public void ofNull() {
        PhotoProperty.of(null);
    }

}
