Slovník [WEB2CZ], verze 1.0.0
http://web2cz.forrest79.net, web2cz@forrest79.net
28.11.2006

Tento zdrojovy kod je sirem pod licenci GNU/GPL! Viz soubor gpl.txt

SEZNAM SOUBORU
--------------
adresar                       soubor                   verze   zmenen       popis
-------                       ------                   -----   ------       -----
doc                           readme.txt               1.0.0   28.11.2006   tento soubor, instalace, informace
doc                           gpl.txt                                       text licence GNU/GPL
doc                           version.txt              1.0.0   28.11.2006   popis zmen u jednotlivych verzi
j2me/Slovnik_WEB2CZ/res       *                                28.11.2006   adresar obsahuje zdroje pro slovnik (obrazky
                                                                            a texty)
j2me/Slovnik_WEB2CZ/src       CheckVersion.java        1.0.0   28.11.2006   trida, slouzici k pripojeni k internetu a
                                                                            zjisteni aktualni verze
j2me/Slovnik_WEB2CZ/src       Loading.java             1.0.0   28.11.2006   trida, implementujici uvodni obrazovku
j2me/Slovnik_WEB2CZ/src       Results.java             1.0.0   28.11.2006   trida, zprostredkovavajici zobrazeni vysledku
                                                                            hledani
j2me/Slovnik_WEB2CZ/src       Searching.java           1.0.0   28.11.2006   trida, realizujici samotne hledani jak v
                                                                            off-line slovniku, tak i na internetu
j2me/Slovnik_WEB2CZ/src       Settings.java            1.0.0   28.11.2006   trida k ulozeni a nacteni nastaveni
j2me/Slovnik_WEB2CZ/src       Slovnik_WEB2CZ.java      1.0.0   28.11.2006   hlavni trida, realizujici beh cele aplikace
java/PrepareDictionary/bin/   PrepareDictionary.java   1.0.0   28.11.2006   trida, zpracovavajici textovy slovnik z
                                                                            http://slovnik.zcu.cz a prevadejici ho do
                                                                            podoby citelne mobilnim slovnikem
java/PrepareDictionary/bin/   PrepareDictionary.class  1.0.0   28.11.2006   zkompilovana trida
php                           congif.php               1.0.0   28.11.2006   nastaveni databaze pro zpristupneni
                                                                            internetoveho slovniku
php                           j2me_dictionary.php      1.0.0   28.11.2006   zpristupneni slovniku pro hledani na internetu
php                           version.php              1.0.0   28.11.2006   predava aktulani verzi
php                           search.php               1.0.0   28.11.2006   rozhrani pro j2me_dictionary.php

INSTALACE
---------
1) Stahnete si z webove adresy http://slovnik.zcu.cz soubor slovnik_data.txt a nakopirujte ho do adresare
   java/PrepareDictionary/bin/
2) Spustte program PrepareDictionary v adresari java/PrepareDictionary/bin/ s nasledujicimi parametry
   "java PrepareDictionary [zdrojovy soubor, defaul: slovnik_data.txt] [vystup anglicke casti, default: eng] 
   [vystup ceske casti, defaul: cze] [kolikate kazde slovicko se ma pouzit, default: 1] [kolik slovicek ulozit do
   slovniku, default: "vsechny"]
3) Prejmenujete soubor s ceskou casti na "cze" a anglickou na "eng" a oba soubory zkopirujte do adresare
   j2me/Slovnik_WEB2CZ/res.
4) Stahnete a nainstalujte si Sun Java Wireless Toolkit 2.2 (http://java.sun.com/javame/downloads) a spustte program
   KToolbar. Vyberte New project... a zadejte Project name: Slovnik_J2ME a MIDlet Class Name: Slovnik_J2ME.
5) Zkopirujte adresare j2me/Slovnik_WEB2CZ/res a j2me/Slovnik_WEB2CZ/src do C:\Programovani\WTK22\apps\Slovnik_WEB2CZ,
   popripade jineho, kam jste nainstalovali Sun Java Wireless Toolkit 2.2.
7) Pro zprovozneni hledani na internetu si musite stahnout balik WEB2CZ (PHP) (http://web2cz.forrest79.net/zdroj.php),
   zprovoznit ho na vasem webserveru a k baliku pridat soubory z adresare php. Nasledne jeste musite ve zdrojovem kodu
   v souboru Slovnik_WEB2CZ.java zmenit adresu v promenne "private static final String URL" na adresu, kam jste soubory
   umistili.
6) Tot vse, nyni muzete program prelozit tlacitkem Build a spustit pomoci Run.

INFORMACE
---------
Program Slovnik [WEB2CZ] obsahuje off-line slovnik a moznost hledat na internetu. Jako zdrojova data pro slovnik je pouzit
GNU/FDL Anglicko - Cesky slovnik (http://slovnik.zcu.cz). Pro mobilni verzi je slovnik upraven nasledovne: vynechana slovicka
kratsi nez 2 znaky a delsi nez 20 znaku, vynechana slovicka obsahujici mezeru, cislo, velke pismeno ci znak, ktery neni pismenko.
Pokud by nekdo vedel, jak slovnik jeste zestihlit. Nejaky algoritmus na vynechani nepodstatnych slovicek nebo neco takoveho, budu
moc rad, za jakykoliv napad. Tento program je muj prvni pro mobilni telefony, proto budu rad za jakoukoliv radu na optimalizaci ci
zrychleni kodu, vylepseni a podobne.
Tesim se na vase reakce na emailu web2cz@forrest79.net.

Jakub Trmota