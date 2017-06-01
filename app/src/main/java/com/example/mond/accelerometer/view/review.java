package com.example.mond.accelerometer.view;


public class review {
    /*
    погано організована структура проекту.
    має бути SplashActivity на якій визначається стан логінізації користувача, в реальних проектах тут можливе завантаження даних і анімація.
    з SplashActivity користувач попадає на LoginActivity, якщо не залогінений, або MainActivity, якщо залогінений.
    MainActivity може мати список з сесіями користувача. Клік на сесію, відкривається SessionDetailActivity з детальна інформація про сесію
    (list даних акселерометра, graph даних акселерометра) це можуть бути різні фрагменти
    В SessionDetailActivity підключаєшся до вітки і слухаєш дані сесії і зразу оновляєш дані SessionDetailListFragment, SessionDetailGraphFragment

     На Firebase окрема вітка з сесіями для кожного користувача, userID-sessionID-><Session information>
     окрема вітка з даними кожної сесії для кожного користувача. userID-sessionID->accelerometerData[]
        сесія стартує, зразу пушиш нову сесію у firebase, і починаєш пушити нові дані у вітку цієї сесії.

    Login & Create Account are different thing, don't try to combine them
      */
}
