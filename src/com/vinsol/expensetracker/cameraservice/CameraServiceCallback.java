package com.vinsol.expensetracker.cameraservice;

public interface CameraServiceCallback {
	/**
	 * Is called when the shutter has been closed during the picture acquisition
	 */
	void onShutter();
	/**
	 * Is called when a picture has been taken by the CameraService
	 * @param imageData The data of the image that has been taken
	 */
	void pictureTaken(byte[] imageData);
}