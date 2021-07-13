package operation;

import javafx.scene.image.ImageView;
import management.CardsManager;
import management.SettingsController;
import management.WindowController;
import settings.Settings;


public class AutoSaveThread extends Thread implements Settings {
    int delay;
    final CardsManager cardsManager;

    public AutoSaveThread(int delay, CardsManager cardsManager){
        this.delay = delay;
        this.cardsManager = cardsManager;
    }

    @Override
    public void run() {
        boolean threadInterrupted = false;
        while(!threadInterrupted && SettingsController.autoSave){
            synchronized (cardsManager) {
                FileReader.writeCards(wordPath, cardsManager.autoSaveIterator());
            }
            try {
                if(WindowController.synchroIcon != null) {
                    WindowController.synchroIcon.setVisible(true);
                }
                sleep(1000);
                if(WindowController.synchroIcon != null){
                    WindowController.synchroIcon.setVisible(false);
                }
                sleep(delay - 1000);
            } catch (InterruptedException e) {
                System.out.println("[WARNING]End of AutoSaving");
                threadInterrupted = true;
            }
            System.out.println("[INFO]Auto-saving");
        }
        System.out.println("[INFO]Thread is done");
    }
}
