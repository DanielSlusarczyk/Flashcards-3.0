package operation;

import management.CardsManager;
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
        while(true){
            synchronized (cardsManager) {
                FileReader.writeCards(wordPath, cardsManager.autoSaveIterator());
            }
            try {
                sleep(delay);
            } catch (InterruptedException e) {
                System.out.println("End of AutoSaving");
            }
            System.out.println("[INFO]Auto-saving");
        }
    }
}