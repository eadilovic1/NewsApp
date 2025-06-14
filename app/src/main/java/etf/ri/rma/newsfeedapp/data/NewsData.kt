package etf.ri.rma.newsfeedapp.data

import etf.ri.rma.newsfeedapp.model.NewsItem

object NewsData {
    private val newsItems = listOf(
        NewsItem(
            uuid = "0",
            title = "Gradonačelnik Sarajeva najavio digitalnu reformu uprave",
            snippet = "Reforma podrazumijeva prelazak svih dokumenata na digitalni oblik do 2027.",
            imageUrl = "",
            category = "Politika",
            isFeatured = true,
            source = "Politički glasnik",
            publishedDate = "03-04-2024"
        ),
        NewsItem(
            uuid = "1",
            title = "Rukometna reprezentacija Njemačke ulazi u finale",
            snippet = "Napeta utakmica odlučila je sudbinu polufinala Evropskog prvenstva koje se igra u Sloveniji.",
            imageUrl = "",
            category = "Sport",
            isFeatured = false,
            source = "Sportski dnevnik",
            publishedDate = "12-05-2025"
        ),
        NewsItem(
            uuid = "2",
            title = "Otvorena prva digitalna ambasada",
            snippet = "Nova inicijativa smanjuje troškove i ubrzava usluge državljanima u inostranstvu.",
            imageUrl = "",
            category = "Politika",
            isFeatured = false,
            source = "Global politika",
            publishedDate = "31-03-2025"
        ),
        NewsItem(
            uuid = "3",
            title = "Otkriven novi tip baterije s dužim trajanjem",
            snippet = "Nova litijum-gvožđe tehnologija mogla bi zamijeniti klasične baterije.",
            imageUrl = "",
            category = "Nauka/Tehnologija",
            isFeatured = false,
            source = "Tech Lab",
            publishedDate = "21-05-2024"
        ),
        NewsItem(
            uuid = "4",
            title = "Timo Boll porazio Ma Longa",
            snippet = "Spektakularan meč oduševio je gledaoce širom svijeta.",
            imageUrl = "",
            category = "Sport",
            isFeatured = true,
            source = "Pongfinity",
            publishedDate = "13-03-2025"
        ),
        NewsItem(
            uuid = "5",
            title = "Novi zakon o transparentnosti finansija",
            snippet = "Parlament usvojio zakon koji zahtijeva potpunu javnost budžetskih troškova.",
            imageUrl = "",
            category = "Politika",
            isFeatured = true,
            source = "Dnevna politika",
            publishedDate = "10-05-2024"
        ),
        NewsItem(
            uuid = "6",
            title = "Zrinka Ljutić najbolja slalomašica svijeta",
            snippet = "Prvi kristalni globus za Hrvatsku nakon Ivice Kostelića.",
            imageUrl = "",
            category = "Sport",
            isFeatured = true,
            source = "Skijanje danas",
            publishedDate = "27-03-2025"
        ),
        NewsItem(
            uuid = "7",
            title = "Političari se sukobili tokom TV debate",
            snippet = "Debata pred izbore donijela neočekivane tenzije.",
            imageUrl = "",
            category = "Politika",
            isFeatured = false,
            source = "Izborna tribina",
            publishedDate = "26-04-2024"
        ),
        NewsItem(
            uuid = "8",
            title = "Nova aplikacija spašava živote u hitnim situacijama",
            snippet = "Aplikacija automatski poziva pomoć i šalje lokaciju.",
            imageUrl = "",
            category = "Nauka/Tehnologija",
            isFeatured = false,
            source = "StartUp Novine",
            publishedDate = "03-05-2025"
        ),
        NewsItem(
            uuid = "9",
            title = "Prva bežična elektrana testirana uspješno",
            snippet = "Energetski sektor pred velikom prekretnicom.",
            imageUrl = "",
            category = "Nauka/Tehnologija",
            isFeatured = true,
            source = "EkoTeh",
            publishedDate = "18-04-2024"
        ),
        NewsItem(
            uuid = "10",
            title = "Premijer najavio novu strategiju za mlade",
            snippet = "Fokus na obrazovanje, zapošljavanje i digitalne vještine.",
            imageUrl = "",
            category = "Politika",
            isFeatured = false,
            source = "Društveni dnevnik",
            publishedDate = "25-05-2025"
        ),
        NewsItem(
            uuid = "11",
            title = "Autonomna vozila stižu na domaće puteve",
            snippet = "Testna flota već se kreće ulicama glavnog grada.",
            imageUrl = "",
            category = "Nauka/Tehnologija",
            isFeatured = false,
            source = "AutoTeh",
            publishedDate = "08-03-2024"
        ),
        NewsItem(
            uuid = "12",
            title = "Gol godine: Nezaboravan trenutak za mladog Amara Gigovića",
            snippet = "Navijači širom Evrope još komentarišu ovaj nevjerovatan pogodak.",
            imageUrl = "",
            category = "Sport",
            isFeatured = true,
            source = "Fudbalski list",
            publishedDate = "21-03-2025"
        ),
        NewsItem(
            uuid = "13",
            title = "Usvojen zakon o zabrani plastike za jednokratnu upotrebu",
            snippet = "Vlada odlučila da krene ka održivijem društvu.",
            imageUrl = "",
            category = "Politika",
            isFeatured = true,
            source = "Eko Vijesti",
            publishedDate = "15-05-2024"
        ),
        NewsItem(
            uuid = "14",
            title = "Nova tehnologija čita misli uz pomoć EEG signala",
            snippet = "Istraživači najavili mogućnosti komunikacije za osobe sa paralizom.",
            imageUrl = "",
            category = "Nauka/Tehnologija",
            isFeatured = true,
            source = "Naučna panorama",
            publishedDate = "09-03-2025"
        ),
        NewsItem(
            uuid = "15",
            title = "Trener NK Bosna Visoko podnio ostavku nakon teškog poraza u desetom kolu premijer lige",
            snippet = "Poraz donio timu pad na tabeli u zonu relegacije.",
            imageUrl = "",
            category = "Sport",
            isFeatured = false,
            source = "Sportski glas",
            publishedDate = "18-05-2024"
        ),
        NewsItem(
            uuid = "16",
            title = "Novo partnerstvo za razvoj pametnih gradova",
            snippet = "Lokalne vlasti udružile snage sa tehnološkim gigantima.",
            imageUrl = "",
            category = "Politika",
            isFeatured = false,
            source = "Urbane politike",
            publishedDate = "24-05-2025"
        ),
        NewsItem(
            uuid = "17",
            title = "Nova laboratorija za kvantne eksperimente",
            snippet = "Otvorena najsavremenija laboratorija u regionu.",
            imageUrl = "",
            category = "Nauka/Tehnologija",
            isFeatured = false,
            source = "Kvantne novosti",
            publishedDate = "16-03-2024"
        ),
        NewsItem(
            uuid = "18",
            title = "Ovo je probna vijest koja testira overflow za naslov i snippet kod \"StandardNewsCard\" koji trebaju biti dovoljno dugi da bi se to postiglo!",
            snippet = "Nikako ne mogu da smislim dobar opis za neku vijest koji ima smisla i pored toga dobro testira šta se desi kad je opis \"standardne vijesti\" duži od 3 reda.",
            imageUrl = "",
            category = "Nauka/Tehnologija",
            isFeatured = false,
            source = "KreativnostiBez",
            publishedDate = "24-05-2025"
        ),
        NewsItem(
            uuid = "19",
            title = "Ovo je probna vijest koja testira overflow za naslov i snippet kod \"FeaturedNewsCard\" koji trebaju biti dovoljno dugi da bi se to postiglo!",
            snippet = "Nikako ne mogu da smislim dobar opis za neku vijest koji ima smisla i pored toga dobro testira šta se desi kad je opis \"istaknute vijesti\" duži od 2 reda.",
            imageUrl = "",
            category = "Nauka/Tehnologija",
            isFeatured = true,
            source = "KreativnostiBez",
            publishedDate = "10-05-2025"
        ),
    )
    fun getAllNews(): List<NewsItem> {
        return newsItems
    }
}