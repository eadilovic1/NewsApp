package etf.ri.rma.newsfeedapp.data

import etf.ri.rma.newsfeedapp.model.NewsItem

object NewsDataWeb {
    private val newsItems = listOf(
        NewsItem(
            uuid = "f78190b6-121e-44d1-9d70-bd07b8da4552",
            title = "Witness describes the attack on Israeli hostage advocates in Colorado",
            snippet = "Witness Brooke Coffman describes fire-related attack on Israeli advocates in Boulder",
            imageUrl = "https://media-cldnry.s-nbcnews.com/image/upload/t_nbcnews-fp-1200-630,f_auto,q_auto:best/mpx/2704722219/2025_06/Witness_still-bgqkj6.jpg",
            category = "general",
            isFeatured = true,
            source = "nbcnews.com",
            publishedDate = "02-06-2025"
        ),
        NewsItem(
            uuid = "113d88bd-d3d8-41f2-9a3f-302e220e7aa4",
            title = "Who Is Playing Harry Potter Leads Harry, Ron and Hermione in HBO Show",
            snippet = "HBO announced the lead cast of its upcoming 'Harry Potter' show after Daniel Radcliffe, Emma Watson and Rupert Grint originated the roles",
            imageUrl = "https://www.usmagazine.com/wp-content/uploads/2025/05/Who-Is-Playing-Harry-Hermione-and-Ron.jpg?w=1200&h=630&crop=1&quality=86&strip=all",
            category = "entertainment",
            isFeatured = true,
            source = "usmagazine.com",
            publishedDate = "27-05-2025"
        ),
        NewsItem(
            uuid = "d8958503-5c02-4c43-9e13-c8c646247621",
            title = "Projected NBA draft pick Mackenzie Mgbako opts for Texas A&M",
            snippet = "Mackenzie Mgbako, a projected second-round pick in the NBA draft, has opted to return to college and spend his junior season at Texas A&M",
            imageUrl = "https://a2.espncdn.com/combiner/i?img=/photo/2025/0527/r1498967_1296x729_16-9.jpg",
            category = "sports",
            isFeatured = true,
            source = "espn.com",
            publishedDate = "27-05-2025"
        ),
        NewsItem(
            uuid = "9cf902b2-4d0f-42bb-90e4-11ac218b494c",
            title = "The deluxe Scott Pilgrim graphic novel box set is cheaper than ever",
            snippet = "Save over 60 percent on Brian O'Malley's Scott Pilgrim 20th anniversary box set",
            imageUrl = "https://platform.theverge.com/wp-content/uploads/sites/2/2025/05/scottpilgrimdailydeal.jpg?quality=90&strip=all&crop=0,10.732984293194,100,78.534031413613&w=1200",
            category = "tech",
            isFeatured = false,
            source = "theverge.com",
            publishedDate = "27-05-2025"
        ),
        NewsItem(
            uuid = "df4ad427-a672-4c67-b6c6-6f81aa00e164",
            title = "Tesla stock jumps after announcement it will join S&P 500 in one go",
            snippet = "Tesla's stock price surged early Tuesday after the company announced it will join S&P 500",
            imageUrl = "https://nypost.com/wp-content/uploads/sites/2/2020/12/tesla-52.jpg?quality=90&strip=all&w=1200",
            category = "business",
            isFeatured = true,
            source = "nypost.com",
            publishedDate = "01-12-2020"
        ),
        NewsItem(
            uuid = "53bd2a00-be63-4636-962e-c8fa4fdd748f",
            title = "Kiev’s actions harming peace process – Kremlin",
            snippet = "Spokesman Dmitry Peskov has cited a recent uptick in Ukrainian UAV attacks on Russian territory, echoing a statement by the Defense Ministry",
            imageUrl = "https://mf.b37mrtl.ru/files/2025.05/article/6835b47385f54055334c28d3.jpg",
            category = "general",
            isFeatured = true,
            source = "rt.com",
            publishedDate = "27-05-2025"
        ),
        NewsItem(
            uuid = "fe902cef-4b2f-499a-bfb3-4d3f852fa4dc",
            title = "Mary Earps: England GK makes shock call to retire before Euro 2025",
            snippet = "England goalkeeper Mary Earps has announced her shock decision to retire from international football",
            imageUrl = "https://a.espncdn.com/combiner/i?img=/photo/2025/0527/r1498906_1296x729_16-9.jpg",
            category = "sports",
            isFeatured = true,
            source = "espn.co.uk",
            publishedDate = "27-05-2025"
        ),
        NewsItem(
            uuid = "a414afc7-46f5-457b-8ed7-304ba9b6d720",
            title = "Barefoot NYC crypto torture victim begs cop for help after fleeing captors, shocking video shows",
            snippet = "Cops have charged Kentucky crypto entrepreneur John Woeltz with kidnapping and assault in the case",
            imageUrl = "https://nypost.com/wp-content/uploads/sites/2/2025/05/crypto-king-kidnapped-tortured-victim-comp.jpg?quality=75&strip=all&w=1024",
            category = "general",
            isFeatured = true,
            source = "nypost.com",
            publishedDate = "27-05-2025"
        ),
        NewsItem(
            uuid = "1f06ad38-8246-4458-ba37-c89fdefa3965",
            title = "Penns Woods Bancorp (PWOD) Maintains Quarterly Dividend",
            snippet = "Penns Woods Bancorp continues its stable quarterly dividend with a 4.39% forward yield",
            imageUrl = "https://static.gurufocus.com/images/global_logo_twitter_card.png",
            category = "business",
            isFeatured = false,
            source = "gurufocus.com",
            publishedDate = "27-05-2025"
        ),
        NewsItem(
            uuid = "ecb04203-14af-420a-9ae3-4cbaa8f48d31",
            title = "Social media star \"The Dogist\" talks new book, online fame",
            snippet = "The Dogist social media star discusses his new book and capturing America's love for dogs",
            imageUrl = "https://assets3.cbsnewsstatic.com/hub/i/r/2025/05/31/fdd8ffbc-d912-4ddf-88ea-293dff3a8b9c/thumbnail/1200x630/89d3d8c215e3c67fb67d67ead21c4d85/0531-satmo-jacobson.jpg?v=15603985a4508c8e7bc83d8e4e73c5d0",
            category = "entertainment",
            isFeatured = true,
            source = "cbsnews.com",
            publishedDate = "31-05-2025"
        )
    )

    fun getAllNews(): List<NewsItem> {
        return newsItems
    }
}