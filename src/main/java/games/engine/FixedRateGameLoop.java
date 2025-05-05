package games.engine;

public class FixedRateGameLoop extends Thread{
    private final double FPS;
    private final long frameTimeNS;
    private volatile boolean running = false;

    private final Runnable tick;

    public FixedRateGameLoop(double FPS, Runnable tick) {
        this.FPS = FPS;
        this.tick = tick;
        this.frameTimeNS = (long)(1_000_000_000/FPS);
    }
    @Override
    public void run() {
        long lastTime = System.nanoTime();
        running = true;
        while (running) {
            long now = System.nanoTime();
            long elapsed = now - lastTime;

            if (elapsed >= frameTimeNS) {
                lastTime = now;
                tick.run();//Call update/render
            } else {
                long sleepTime = (frameTimeNS - elapsed) / 1_000_000;
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }
    public void stopLoop() {
        running = false;
    }

}
