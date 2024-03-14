package swan.g09.cs230a2;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;

import javafx.geometry.Point2D;

@ExtendWith(MockitoExtension.class)
class PinkBallTest {
    @Test
    void moveInterval() {
        PinkBall pinkBall = Mockito.spy(new PinkBall(Point2D.ZERO));
        for (int i = 0; i < 1000; i++) {
            pinkBall.tick();
            assertEquals(1000/PinkBall.MOVE_INTERVAL, actual);
        }
    }
}