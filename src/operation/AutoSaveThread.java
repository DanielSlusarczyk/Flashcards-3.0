package operation;

import management.CardsManager;
import controllers.SettingsController;
import controllers.WindowController;
import settings.Settings;

public class AutoSaveThread extends Thread implements Settings {
    final CardsManager cardsManager;
    //Basic:
    int delay;

    public AutoSaveThread(int delay, CardsManager cardsManager) {
        this.delay = delay;
        this.cardsManager = cardsManager;
    }

    @Override
    public void run() {
        boolean threadInterrupted = false;
        while (!threadInterrupted && SettingsController.autoSave) {
            synchronized (cardsManager) {
                FileReader.writeCards(cardsManager.autoSaveIterator());
            }
            try {
                if (WindowController.synchroIcon != null) {
                    WindowController.synchroIcon.setVisible(true);
                }
                sleep(2000);
                if (WindowController.synchroIcon != null) {
                    WindowController.synchroIcon.setVisible(false);
                }
                sleep(delay - 2000);
            } catch (InterruptedException e) {
                System.out.println("[WARNING]End of AutoSaving");
                threadInterrupted = true;
            }
            System.out.println("[INFO]Auto-saving");
        }
        System.out.println("[INFO]Thread is done");
    }
}
