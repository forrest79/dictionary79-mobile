// Settings - 1.0.0 - 28.11.2006
import javax.microedition.lcdui.*;
import javax.microedition.rms.*;

public class Settings {

	private Slovnik_WEB2CZ midlet = null;

	private RecordStore settings = null;

	private static final int DICTIONARY_DIRECTION = 1;
	private static final int DICTIONARY_SIMILAR = 2;
	private static final int NET_DIRECTION = 3;
	private static final int NET_SETTINGS_SIMILAR = 4;
	private static final int NET_SETTINGS_LOGICAL = 5;

	public Settings(Slovnik_WEB2CZ midlet) {
		this.midlet = midlet;
	}

	public void openDBSettings() {
		try {
			settings = RecordStore.openRecordStore("settings", true);
		} catch(Exception e) {}
	}

	public void closeDBSettings() throws RecordStoreNotOpenException, RecordStoreException {
		try {
			settings.closeRecordStore();
		} catch(Exception e) {}
	}

	public void loadSettings() {
		String settDictionary_direction = null;
		String settDictionary_settings_similar = null;
		String settNet_direction = null;
		String settNet_settings_similar = null;
		String settNet_settings_logical = null;

		try {
			if(settings.getNumRecords() == 0) {
				String settingsValue;
				byte[] byteSettingsValue = null;

				int new_settings;

				settingsValue = "0";
				byteSettingsValue = settingsValue.getBytes();
				new_settings = settings.addRecord(byteSettingsValue, 0, byteSettingsValue.length); // DICTIONARY_DIRECTION

				settingsValue = "1";
				byteSettingsValue = settingsValue.getBytes();
				new_settings = settings.addRecord(byteSettingsValue, 0, byteSettingsValue.length); // DICTIONARY_SIMILAR

				settingsValue = "0";
				byteSettingsValue = settingsValue.getBytes();
				new_settings = settings.addRecord(byteSettingsValue, 0, byteSettingsValue.length); // NET_DIRECTION

				settingsValue = "1";
				byteSettingsValue = settingsValue.getBytes();
				new_settings = settings.addRecord(byteSettingsValue, 0, byteSettingsValue.length); // NET_SETTINGS_SIMILAR

				settingsValue = "0";
				byteSettingsValue = settingsValue.getBytes();
				new_settings = settings.addRecord(byteSettingsValue, 0, byteSettingsValue.length); // NET_SETTINGS_LOGICAL
			}

			byte[] dictionary_direction = new byte[settings.getRecordSize(DICTIONARY_DIRECTION)];
			settings.getRecord(DICTIONARY_DIRECTION, dictionary_direction, 0);
			settDictionary_direction = new String(dictionary_direction);

			byte[] dictionary_settings = new byte[settings.getRecordSize(DICTIONARY_SIMILAR)];
			settings.getRecord(DICTIONARY_SIMILAR, dictionary_settings, 0);
			settDictionary_settings_similar = new String(dictionary_settings);

			byte[] net_direction = new byte[settings.getRecordSize(NET_DIRECTION)];
			settings.getRecord(NET_DIRECTION, net_direction, 0);
			settNet_direction = new String(net_direction);

			byte[] net_settings_similar = new byte[settings.getRecordSize(NET_SETTINGS_SIMILAR)];
			settings.getRecord(NET_SETTINGS_SIMILAR, net_settings_similar, 0);
			settNet_settings_similar = new String(net_settings_similar);

			byte[] net_settings_logical = new byte[settings.getRecordSize(NET_SETTINGS_LOGICAL)];
			settings.getRecord(NET_SETTINGS_LOGICAL, net_settings_logical, 0);
			settNet_settings_logical = new String(net_settings_logical);

			if(settDictionary_direction.compareTo("1") == 0) midlet.frmDictionarySearch_chgDirection.setSelectedIndex(0, true);
			else if(settDictionary_direction.compareTo("2") == 0) midlet.frmDictionarySearch_chgDirection.setSelectedIndex(1, true);

			if(settDictionary_settings_similar.compareTo("1") == 0) midlet.frmDictionarySearch_chgSettings.setSelectedIndex(0, true);

			if(settNet_direction.compareTo("1") == 0) midlet.frmNetSearch_chgDirection.setSelectedIndex(0, true);
			else if(settNet_direction.compareTo("2") == 0) midlet.frmNetSearch_chgDirection.setSelectedIndex(1, true);
			else if(settNet_direction.compareTo("3") == 0) midlet.frmNetSearch_chgDirection.setSelectedIndex(2, true);

			if(settNet_settings_similar.compareTo("1") == 0) midlet.frmNetSearch_chgSettings.setSelectedIndex(0, true);

			if(settNet_settings_logical.compareTo("1") == 0) midlet.frmNetSearch_chgSettings.setSelectedIndex(1, true);
		} catch (Exception e) {
			return;
		}
	}


	public void saveSettings() {
		try {
			String settingsValue = "0";
			byte[] byteSettingsValue = null;

			settingsValue = "0";
			if(midlet.frmDictionarySearch_chgDirection.getSelectedIndex() == 0) settingsValue = "1";
			else if(midlet.frmDictionarySearch_chgDirection.getSelectedIndex() == 1) settingsValue = "2";
			byteSettingsValue = settingsValue.getBytes();
			settings.setRecord(DICTIONARY_DIRECTION, byteSettingsValue, 0, byteSettingsValue.length);

			settingsValue = "0";
			if(midlet.frmDictionarySearch_chgSettings.isSelected(0)) settingsValue = "1";
			byteSettingsValue = settingsValue.getBytes();
			settings.setRecord(DICTIONARY_SIMILAR, byteSettingsValue, 0, byteSettingsValue.length);

			settingsValue = "0";
			if(midlet.frmNetSearch_chgDirection.getSelectedIndex() == 0) settingsValue = "1";
			else if(midlet.frmNetSearch_chgDirection.getSelectedIndex() == 1) settingsValue = "2";
			else if(midlet.frmNetSearch_chgDirection.getSelectedIndex() == 2) settingsValue = "3";
			byteSettingsValue = settingsValue.getBytes();
			settings.setRecord(NET_DIRECTION, byteSettingsValue, 0, byteSettingsValue.length);

			settingsValue = "0";
			if(midlet.frmNetSearch_chgSettings.isSelected(0)) settingsValue = "1";
			byteSettingsValue = settingsValue.getBytes();
			settings.setRecord(NET_SETTINGS_SIMILAR, byteSettingsValue, 0, byteSettingsValue.length);

			settingsValue = "0";
			if(midlet.frmNetSearch_chgSettings.isSelected(1)) settingsValue = "1";
			byteSettingsValue = settingsValue.getBytes();
			settings.setRecord(NET_SETTINGS_LOGICAL, byteSettingsValue, 0, byteSettingsValue.length);
		} catch (Exception e) {
			return;
		}
	}
}