package com.homeaid.worklog.util;

public class GeoUtils {

    private static final double EARTH_RADIUS_KM = 6371.0; // 지구 반지름 (킬로미터)

    /**
     * 두 지점 간의 거리 계산 (단위: 미터)
     *
     * @param reservationLat 예약 건 위도
     * @param reservationLon 예약 건 경도
     * @param lat 위도
     * @param lon 경도
     * @return 거리 (미터 단위)
     */
    public static double calculateDistanceInMeters(double reservationLat, double reservationLon, double lat, double lon) {
        double dLat = Math.toRadians(lat - reservationLat);
        double dLon = Math.toRadians(lon - reservationLon);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(reservationLat)) * Math.cos(Math.toRadians(lat))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double distanceKm = EARTH_RADIUS_KM * c;
        return distanceKm * 1000; // meter
    }
}
