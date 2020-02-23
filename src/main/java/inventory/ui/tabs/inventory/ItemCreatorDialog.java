package inventory.ui.tabs.inventory;

import inventory.model.Item;
import inventory.ui.DialogStage;
import inventory.ui.tabs.inventory.DialogNewItemController;

public class ItemCreatorDialog extends DialogStage {

    @Override
    public String getFXMLName() {
        return "/fxml/dialogs/item_creator.fxml";
    }

    @Override
    public String getDialogTitle() {
        return "New Item";
    }

    public ItemCreatorDialog(Item item) {

        DialogNewItemController controller = getController();
        controller.setItem(item);
    }

}
