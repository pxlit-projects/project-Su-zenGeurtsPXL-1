package be.pxl.services;

import be.pxl.services.domain.Category;
import be.pxl.services.domain.Post;
import be.pxl.services.domain.State;
import be.pxl.services.repository.PostRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.Month;

@Component
public class DataSeeder implements CommandLineRunner {

    private final PostRepository postRepository;
    private static final Logger logger = LoggerFactory.getLogger(DataSeeder.class);

    public DataSeeder(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Override
    public void run(String... args) {
        if (postRepository.count() == 0) {
            logger.info("Seeding posts");
            Post post1 = Post.builder()
                    .title("1.000 Limburgse leerlingen strijden in Lego League op Hogeschool PXL")
                    .content("""
                            De campus van Hogeschool PXL in Hasselt staat donderdag en vrijdag volledig in het teken van techniek en innovatie.
                            Maar liefst duizend enthousiaste leerlingen van 76 scholen uit heel Limburg verzamelden zich hier om deel te
                            nemen aan de twaalfde editie van de regionale finale van de FIRST LEGO League, een prestigieuze internationale wedstrijd.
                            Het internationale evenement wil techniek en innovatie promoten bij kinderen tussen 9 en 14 jaar oud.
                            De kinderen leren omgaan met techniek door zelf robotten te programmeren en hen bepaalde missies te laten uitvoeren.
                            Ook Leela Schoyen (11) en Valerie Veracht (10) van het Go! Freinetschool De Step in Beringen waren aanwezig.
                            “Op een paar strafpunten na, ging het echt goed”, lachen de twee.
                            """)
                    .userId(4L)
                    .category(Category.CAMPUS)
                    .createdAt(LocalDateTime.of(2024, Month.DECEMBER, 1, 8, 30, 0))
                    .state(State.APPROVED)
                    .build();

            postRepository.save(post1);

            Post post2 = Post.builder()
                    .title("PXL-studenten schitteren op Hack The Future hackathon")
                    .content("""
                            PXL–Studenten hebben zich van hun beste kant laten zien tijdens de Hack The Future hackathon,
                            de grootste technologie- en designwedstrijd van België, georganiseerd door technologiebedrijf Cronos.
                            Gijs Rommers en Kiara Petillon, studenten Toegepaste Informatica, wonnen de hoofdprijs in een van de challenges.
                            Gijs en Kiara maakten een webapplicatie genaamd 'Galactic Explorer', waarmee gebruikers een sterrenstelsel kunnen verkennen.
                            Met Laravel en Vue.js ontwikkelden ze een gebruiksvriendelijk en innovatief platform.
                            De jury prees hen voor de sterke technische uitwerking en creatieve aanpak.
                            Ook de studenten van het graduaat Digitale Vormgeving namen deel aan de hackathon.
                            Zij werkten aan design-opdrachten rond het thema ‘space discovery’, zoals het ontwerpen van branding voor een fictieve planeet
                            of het bedenken van een marketingcampagne voor een ruimtereismaatschappij.
                            """)
                    .userId(1L)
                    .category(Category.ACADEMIC)
                    .createdAt(LocalDateTime.of(2024, Month.DECEMBER, 3, 9, 0, 0))
                    .state(State.PUBLISHED)
                    .build();

            postRepository.save(post2);

            Post post3 = Post.builder()
                    .title("Limburgs AI-team, met ook leerkracht Syntra-PXL, levert special effects voor Hollywoodfilm 'Here' met Tom Hanks en Robin Wright")
                    .content("""
                            Pionier en specialist in artificiële intelligentie Chris Umé uit Beringen is zondag met familie,
                            vrienden en z’n Belgische team van Metaphysic naar de film ‘Here’ gaan kijken in Kinepolis Hasselt.
                            In de prent werden hoofdrolspelers Tom Hanks en Robin Wright door Metaphysic zowel (veel) jonger als ouder gemaakt met artificiële intelligentie.
                            Niemand van het team, behalve Chris die op de Amerikaanse première was, had de film tot nog toe gezien. In dat team zaten ook een tiental Belgen,
                            waarvan acht Limburgers: “Er zat ook een klein hecht groepje Limburgers bij waaronder verschillende studenten en een leerkracht van Syntra-PXL”,
                            vertelt Umé. “Als we op zoek gaan naar mensen, is het enthousiasme en de wil om te werken belangrijker dan de studies of een diploma.”
                            """)
                    .userId(4L)
                    .category(Category.STUDENT)
                    .createdAt(LocalDateTime.of(2024, Month.DECEMBER, 5, 15, 48, 0))
                    .state(State.SUBMITTED)
                    .build();

            postRepository.save(post3);

            Post post4 = Post.builder()
                    .title("Naaktkalender van PXL-roeiteam brengt 1.000 euro op voor Kom op tegen Kanker")
                    .content("""
                            De ‘Naaktkalender 2024’ was een bijzondere mijlpaal voor het PXL-roeiteam, dat voor de zevende keer deze kalender samenstelde.
                            De foto's werden dit keer genomen op het congresgebouw van PXL-NeXT in Hasselt door fotograaf Toon Van Den Broek, drukkerij Haletra zorgde voor de druk.
                            Het PXL-roeiteam koos ervoor om de opbrengst van de kalenderverkoop te doneren aan Kom Op Tegen Kanker.
                            Deze keuze, geïnspireerd door hun sportcoördinator Erik Vanmierlo, illustreerde de betrokkenheid van het team.
                            De kalenders waren te koop voor 10 euro per stuk tijdens de schooluren aan de Stuvo-balie van Hogeschool PXL en waren al snel uitverkocht.
                            “Met deze naaktkalender wilden we niet alleen de schoonheid van de sport vieren, maar ook ons steentje bijdragen aan de strijd tegen kanker“, zegt kapitein Lukas Gendera.
                            “We zijn dankbaar voor de steun van onze PXL-gemeenschap en hopen dat de opbrengst van de verkoop van onze kalender een steentje zal bijdragen aan het verder onderzoek tegen kanker.”
                            “Helaas is kanker ook aanwezig in de sportwereld”, zegt Tim Van Werde, districtscoördinator Limburg van Kom op tegen Kanker.
                            “Het doet goed als sportieve jonge mensen ook hun steentje bijdragen om kanker de wereld uit te helpen.
                            De meeste mensen weten wel dat bewegen goed is om hart- en vaatziekten en diabetes te voorkomen. Maar bewegen verkleint ook de kans op kanker.“
                            """)
                    .userId(4L)
                    .category(Category.SPORTS)
                    .createdAt(LocalDateTime.of(2024, Month.DECEMBER, 14, 16, 4, 0))
                    .state(State.SUBMITTED)
                    .build();

            postRepository.save(post4);

            Post post5 = Post.builder()
                    .title("Expo De Research Group")
                    .content("""
                            Het kunstenaarscollectief met Hugo Duchateau, Jos Jans, Hélène Keil, Dré Sprakenis, Vincent Van Den Meersch en Jan Withofs,
                            dat actief was van 1967 tot 1972, creëerde met gebundelde krachten een keerpunt in de hedendaagse kunst in Limburg.
                            Via opvallende en speelse performances en groepsexpo’s maakten ze een inhaalbeweging ten opzichte van de internationale kunstscène.
                            Vijftig jaar later zijn de sporen nog steeds voelbaar en de werken nog steeds relevant. Tijd voor een terugblik!
                            Opening tentoonstelling op vrijdag 20 september 2024 om 19.00 uur in Het Stadsmus, Hasselt. Van harte welkom!
                            Gelieve je aanwezigheid hier te registreren vóór 15 september 2024.
                            De avond van de opening heb je de mogelijkheid om de bijhorende publicatie aan te kopen. Een parel van auteur Edith Doove en vormgegeven door Geoffrey Brussatto.
                            De tentoonstelling en publicatie kwamen tot stand in samenwerking met School of Arts PXL-MAD, met steun van de Vlaamse Gemeenschap.
                            """)
                    .userId(4L)
                    .category(Category.RESEARCH)
                    .createdAt(LocalDateTime.of(2024, Month.DECEMBER, 10, 10, 32, 0))
                    .state(State.PUBLISHED)
                    .build();

            postRepository.save(post5);

            Post post6 = Post.builder()
                    .title("Alumni PXL-MAD genomineerd voor Global Creative Graduate Showcase 2024")
                    .content("""
                            Tyana Verstraete en Jiawei Xu, alumni van School of Arts PXL-MAD, zijn genomineerd voor de Global Creative Graduate Showcase 2024 in samenwerking met WGSN,
                            Coloro + Google Arts & Culture. De publieksstemming is officieel geopend tot en met vrijdag 11 oktober. De winnaars worden op maandag 14 oktober bekendgemaakt.
                            """)
                    .userId(3L)
                    .category(Category.ALUMNI)
                    .createdAt(LocalDateTime.of(2024, Month.DECEMBER, 8, 14, 56, 0))
                    .state(State.DRAFTED)
                    .build();

            postRepository.save(post6);
        }
    }
}

