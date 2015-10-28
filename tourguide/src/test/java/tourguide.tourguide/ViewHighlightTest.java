package tourguide.tourguide;

import android.view.View;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ViewHighlightTest {
    @Mock
    View view;

    ViewHighlight viewHighlight;

    @Before
    public void setUp() throws Exception {
        when(view.getWidth()).thenReturn(100);
        when(view.getHeight()).thenReturn(200);
    }

    @Test
    public void should_set_radius() {
        viewHighlight = ViewHighlight.from(view, ViewHighlight.Style.CIRCLE);
        assertThat(viewHighlight.getRadius()).isEqualTo(100);
    }
}
