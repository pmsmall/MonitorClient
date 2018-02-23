package com.net.capture.util;

import java.awt.Dialog;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

public class FileHelper {
	public static final FileFilter dllFilter;
	public static final int OPEN = JFileChooser.OPEN_DIALOG;
	public static final int SAVE = JFileChooser.SAVE_DIALOG;

	static {
		dllFilter = new DllFilter();
	}

	public static File getFile(Dialog dialog, String title, int type, FileFilter filter, String currentDirectory) {
		JFileChooser saveDialog;

		if (currentDirectory != null)
			saveDialog = new JFileChooser(currentDirectory);
		else
			saveDialog = new JFileChooser();
		// (dialog, title, mode);
		saveDialog.setDialogTitle(title);
		saveDialog.setDialogType(type);
		if (filter != null)
			saveDialog.setFileFilter(filter);

		int result = saveDialog.showOpenDialog(dialog);
		if (result == JFileChooser.APPROVE_OPTION) {
			File selectFile = saveDialog.getSelectedFile();
			if (selectFile.isFile()) {
				return selectFile;
			}
		}
		return null;
	}

	public static File getFile(Dialog dialog, String title, int type, FileFilter filter) {
		return getFile(dialog, title, type, filter, null);
	}

	public static File getFile(String title, int type, FileFilter filter) {
		return getFile(null, title, type, filter, null);
	}

	public static File getFile(String title, int type) {
		return getFile(null, title, type, null, null);
	}

	private static class DllFilter extends FileFilter {

		@Override
		public boolean accept(File pathname) {
			if (pathname.isDirectory() | pathname.getPath().endsWith(".dll"))
				return true;
			return false;
		}

		@Override
		public String getDescription() {
			return "动态链接库dll文件";
		}

	}

}
