package com.example.mediabrowserplayer.data

object TracksList {
    val tracks = mutableListOf<Track>()
    val podcasts = mutableListOf<Track>()

    init {
        tracks.add(Track("http://162.244.80.118:4900/stream.mp3?ver=604498", "FM 100 Pakistan Islamabad", "Radio Description for FM 100 Pakistan Islamabad", "http://fm100pakistan.com/favicon.ico"))
        tracks.add(Track("https://samaakhi107-itelservices.radioca.st/stream", "Sama Akhi FM 107", "Radio Description for Sama Akhi FM 107", "https://cdn-profiles.tunein.com/s182923/images/logog.png"))
        tracks.add(Track("https://radio.cityfm89.com/stream", "City FM 98", "Radio description for City FM 98", "https://i1.sndcdn.com/avatars-000204080367-iyzzq2-t500x500.jpg"))
        tracks.add(Track("http://162.244.80.118:4900/stream.mp3", "FM 100 Pakistan Abbottabad", "Radio description for FM 100 Pakistan Abbottabad", "https://static-media.streema.com/media/cache/31/94/31944dae18c19414f1f146b208db0a01.jpg"))


        podcasts.add(Track("https://traffic.libsyn.com/secure/cybersecuritytoday/CYBER_SECURITY_TODAY_FEB_22_2023.mp3", "Cybersecurity Today", "Hackers selling data centre logins", "https://media.istockphoto.com/id/1335169133/vector/cyber-security-line-icon-shield-with-electronic-components-and-padlock.jpg?s=612x612&w=0&k=20&c=9Wb9umNUOwZm3_vZyt1cfHBxPKi-NIDyenBfJgO7C2M="))
        podcasts.add(Track("https://traffic.libsyn.com/secure/cybersecuritytoday/CYBER_SECURITY_TODAY_FEB_20_2023.mp3", "Cybersecurity Today", "A business email scam group is", "https://previews.123rf.com/images/nexusby/nexusby1712/nexusby171200015/91689622-cyber-security-logo-concept.jpg"))
        podcasts.add(Track("https://traffic.libsyn.com/secure/cybersecuritytoday/CYBER_SECURITY_TODAY_FEB_17_2023.mp3", "Cybersecurity Today", "A fake Emsisoft code-signing certificate", "https://st3.depositphotos.com/4177785/14916/v/600/depositphotos_149163916-stock-illustration-cyber-security-icon.jpg"))
        podcasts.add(Track("https://traffic.libsyn.com/secure/cybersecuritytoday/CYBER_SECURITY_TODAY_FEB_15_2023.mp3", "Cybersecurity Today", "Patches released for Microsoft Exchange", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTH8MszwYqe9eCC2iZS5BTp4DN4KE7UkJgFrA"))
        podcasts.add(Track("https://traffic.libsyn.com/secure/cybersecuritytoday/CYBER_SECURITY_TODAY_FEB_13_2023.mp3", "Cybersecurity Today", "Hole in GoAnywhere file transfer utility exploited", "https://logo.com/image-cdn/images/kts928pd/production/bec988979124aaca66e44172f7af7eab7c5b8226-349x359.png?w=1080&q=72"))
    }

}