package com.faveoffate.glpractice;

import com.faveoffate.glpractice.framework.Game;
import com.faveoffate.glpractice.framework.Input;
import com.faveoffate.glpractice.framework.Screen;
import com.faveoffate.glpractice.thegame.Camera2D;
import com.faveoffate.glpractice.thegame.Cannon;
import com.faveoffate.glpractice.thegame.DynamicGameObject;
import com.faveoffate.glpractice.thegame.GLGame;
import com.faveoffate.glpractice.thegame.GLGraphics;
import com.faveoffate.glpractice.thegame.GameObject;
import com.faveoffate.glpractice.thegame.OverlapTester;
import com.faveoffate.glpractice.thegame.SpatialHashGrid;
import com.faveoffate.glpractice.thegame.SpriteBatcher;
import com.faveoffate.glpractice.thegame.Texture;
import com.faveoffate.glpractice.thegame.TextureRegion;
import com.faveoffate.glpractice.thegame.Vector2;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

public class SpriteBatcherTest extends GLGame {
    @Override
    public Screen getStartScreen() {
        return new SpriteBatcherScreen(this);
    }

    class SpriteBatcherScreen extends Screen {
        final int NUM_TARGETS = 20;
        final float WORLD_WIDTH = 9.6f;final float WORLD_HEIGHT = 4.8f;
        GLGraphics glGraphics;
        Cannon cannon;
        DynamicGameObject ball;
        List<GameObject> targets;
        SpatialHashGrid grid;
        TextureRegion cannonRegion;
        TextureRegion ballRegion;
        TextureRegion bobRegion;
        SpriteBatcher batcher;
        Vector2 touchPos = new Vector2();
        Vector2 gravity = new Vector2(0,-10);
        Camera2D camera;
        Texture texture;

        public SpriteBatcherScreen(Game game) {
            super(game);
            glGraphics = ((GLGame)game).getGLGraphics();
            cannon = new Cannon(0, 0, 1, 0.5f);
            ball = new DynamicGameObject(0, 0, 0.2f, 0.2f);
            targets = new ArrayList<>(NUM_TARGETS);
            grid = new SpatialHashGrid(WORLD_WIDTH, WORLD_HEIGHT, 2.5f);
            for(int i = 0; i < NUM_TARGETS; i++) {
                GameObject target = new GameObject((float)Math.random() * WORLD_WIDTH,
                        (float)Math.random() * WORLD_HEIGHT,
                        0.5f, 0.5f);
                grid.insertStaticObject(target);
                targets.add(target);
            }
            batcher = new SpriteBatcher(glGraphics, 100);
            camera = new Camera2D(glGraphics, WORLD_WIDTH, WORLD_HEIGHT);
        }
        @Override
        public void present(float deltaTime) {
            GL10 gl = glGraphics.getGL();
            gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
            camera.setViewportAndMatrices();
            gl.glEnable(GL10.GL_BLEND);
            gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
            gl.glEnable(GL10.GL_TEXTURE_2D);
            batcher.beginBatch(texture);
            int len = targets.size();
            for(int i = 0; i < len; i++) {
                GameObject target = targets.get(i);
                batcher.drawSprite(target.position.x, target.position.y, 0.5f, 0.5f, bobRegion);
            }
            batcher.drawSprite(ball.position.x, ball.position.y, 0.2f, 0.2f, ballRegion);
            batcher.drawSprite(cannon.position.x, cannon.position.y, 1, 0.5f, cannon.angle,
                    cannonRegion);
            batcher.endBatch();
        }
        @Override
        public void update(float deltaTime) {
            List<Input.TouchEvent> touchEvents = game.getInput().getTouchEvents();
            game.getInput().getKeyEvents();
            int len = touchEvents.size();
            for (int i = 0; i < len; i++) {
                Input.TouchEvent event = touchEvents.get(i);
                camera.touchToWorld(touchPos.set(event.x, event.y));
                cannon.angle = touchPos.sub(cannon.position).angle();
                if(event.type == Input.TouchEvent.TOUCH_UP) {
                    float radians = cannon.angle * Vector2.TO_RADIANS;
                    float ballSpeed = touchPos.len() * 2;
                    ball.position.set(cannon.position);
                    ball.velocity.x = (float)Math.cos(radians) * ballSpeed;
                    ball.velocity.y = (float)Math.sin(radians) * ballSpeed;
                    ball.bounds.lowerLeft.set(ball.position.x - 0.1f, ball.position.y - 0.1f);
                }
            }
            ball.velocity.add(gravity.x * deltaTime, gravity.y * deltaTime);
            ball.position.add(ball.velocity.x * deltaTime, ball.velocity.y * deltaTime);
            ball.bounds.lowerLeft.add(ball.velocity.x * deltaTime, ball.velocity.y * deltaTime);
            List<GameObject> colliders = grid.getPotentialColliders(ball);
            len = colliders.size();
            for(int i = 0; i < len; i++) {
                GameObject collider = colliders.get(i);
                if(OverlapTester.overlapRectangles(ball.bounds, collider.bounds)) {
                    grid.removeObject(collider);
                    targets.remove(collider);
                }
            }
            if(ball.position.y > 0) {
                camera.position.set(ball.position);
                camera.zoom = 1 + ball.position.y / WORLD_HEIGHT;
            } else {
                camera.position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2);
                camera.zoom = 1;
            }
        }
        @Override
        public void pause() {
        }
        @Override
        public void resume() {
            texture = new Texture(((GLGame)game), "atlas.png");
            cannonRegion = new TextureRegion(texture, 0, 0, 64, 32);
            ballRegion = new TextureRegion(texture, 0, 32, 16, 16);
            bobRegion = new TextureRegion(texture, 32, 32, 32, 32);
        }
        @Override
        public void dispose() {
        }
    }
}
