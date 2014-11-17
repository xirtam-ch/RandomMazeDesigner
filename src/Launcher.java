
public class Launcher {
    public static void main(String[] args) {
        final EnterView view = new EnterView(new MazeUtils());
//        new Thread("refresh") {
//            @Override
//            public void run() {
//                super.run();
//                view.repaint();
//                try {
//                    Thread.sleep(11500);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }.start();
    }
}
