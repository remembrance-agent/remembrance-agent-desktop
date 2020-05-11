package io.p13i.ra.gui;

import io.p13i.ra.RemembranceAgentClient;
import io.p13i.ra.utils.FileIO;

import java.io.File;

import static java.util.prefs.Preferences.userNodeForPackage;

/**
 * Encapsulates user-specific references
 */
public class User {

    public static String NAME = null;

    public static class Home {
        private static String mDirectory = System.getProperty("user.home");
        public static String getDirectory() {
            return mDirectory;
        }

        public static void setDirectory(String directory) {
            mDirectory = directory;
        }

        public static class Documents {
            public static String getDirectory() {
                return FileIO.ensureFolderExists(Home.getDirectory() + File.separator + "Documents");
            }

            public static class RA {
                public static String getDirectory() {
                    return FileIO.ensureFolderExists(Documents.getDirectory() + File.separator + "RA");
                }

                public static class Cache {
                    public static String getDirectory() {
                        return FileIO.ensureFolderExists(RA.getDirectory() + File.separator + "~cache");
                    }
                }

                public static class LocalDocuments {
                    public static String getDirectory() {
                        return FileIO.ensureFolderExists(RA.getDirectory() + File.separator + "local-documents");
                    }
                }
            }
        }
    }

    /**
     * Manages user preference settings
     */
    public static class Preferences {

        private static String NOT_SET = "";

        /**
         * Represents all preferences available to the user
         */
        public enum Preference {
            KeystrokesLogFile("KEYSTROKES_LOG_FILE_PATH_PREFS_NODE_NAME", Home.Documents.RA.getDirectory() + File.separator + "keystrokes.log"),
            RAClientLogFile("RA_CLIENT_LOG_FILE_PATH_PREFS_NODE_NAME", Home.Documents.RA.getDirectory() + File.separator + "ra.log"),
            LocalDiskDocumentsFolderPath("LOCAL_DISK_DOCUMENTS_FOLDER_PATH_PREFS_NODE_NAME", Home.Documents.RA.LocalDocuments.getDirectory()),
            GoogleDriveFolderIDs("GOOGLE_DRIVE_FOLDER_IDS_PREFS_NODE_NAME", NOT_SET),
            GmailMaxEmailsCount("GMAIL_MAX_EMAILS_COUNT_NODE_NAME", "10"),
            UserConsentsToKeylogger("USER_CONSENTS_TO_KEYLOGGER", Boolean.FALSE.toString());

            /**
             * The name of the node used in the backing store
             */
            private String nodeName;

            /**
             * The default value for the item if the node is not found in the backing store
             */
            private String defaultValue;

            Preference(String nodeName, String defaultValue) {
                this.nodeName = nodeName;
                this.defaultValue = defaultValue;
            }
        }

        /**
         * Gets a preference as a string
         *
         * @param preference the preference
         * @return the string value
         */
        public static String getString(Preference preference) {

            for (Preference pref : Preference.values()) {
                if (preference.nodeName.equals(pref.nodeName)) {
                    return userNodeForPackage(RemembranceAgentClient.class).get(preference.nodeName, preference.defaultValue);
                }
            }

            throw new IllegalArgumentException(preference.toString());
        }

        /**
         * Gets a preference as an Integer
         *
         * @param preference the preference to get
         * @return the int value
         */
        public static int getInt(Preference preference) {
            return Integer.parseInt(getString(preference));
        }

        /**
         * Sets the preference value in the backing store
         *
         * @param preference the preference
         * @param value      the value to set it to in the backing store
         */
        public static void set(Preference preference, String value) {
            userNodeForPackage(RemembranceAgentClient.class).put(preference.nodeName, value);
        }

        /**
         * Sets a boolean preference
         * @param preference the key
         * @param value the value
         */
        public static void set(Preference preference, Boolean value) {
            set(preference, value.toString());
        }

        /**
         * Gets a boolean preference
         * @param preference the key
         * @return boolean value
         */
        public static Boolean getBoolean(Preference preference) {
            return Boolean.parseBoolean(getString(preference));
        }
    }
}
