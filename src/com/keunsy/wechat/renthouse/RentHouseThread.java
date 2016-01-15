package com.keunsy.wechat.renthouse;

public class RentHouseThread extends Thread {

    private static RentHouseThread rentHouseThread = new RentHouseThread();

    public static RentHouseThread getInstance() {
	return rentHouseThread;
    }

    @Override
    public void run() {
	DoubanHouseService.getHouseInfoByGroup2(null);
    }

    public static void stopRentHouse() {
	rentHouseThread.interrupt();
    }

}
