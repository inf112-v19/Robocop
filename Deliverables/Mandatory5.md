# Oblig 5

## Deloppgave 1:  Prosjekt og prosjektstruktur 
-	Vi synes valgene vi har tatt er gode. Erfaringsmessig synes vi at valgene vi har tatt er 
    gode valg 
-	Gruppedynamikken er bra, alle kommer godt overens og snakker bra sammen. 
-	Kommunikasjonen mellom medlemmene i gruppen fungerer bra da vi har gode kanaler å kommunisere over. 
-	Referat fra møter: 
```

3. April : Alle tilstede utenom Madelen. 
Entiteter på kart og startet på ny chatboks 

10.april : Alle tilstede utenom Madelen. 
Jobbet med at roboter kan dytte hverandre og ny chatboks ble ferdig. Spille mot AI

23.april : Henning og Maren til stede. 
Refaktorering av kode og jobbet med den skriftlige oppgaven.

24.april : Alle tilstede utenom Madelen og Steffen
Vi jobbet med  at vegger skal stoppe laseren

30.april : Sverre, Henning og Steffen tistede.
Joobet med laser rendering

```
## Retrospektiv av gruppen
* De beslutningene vi i gruppen har tatt i løpet av denne perioden mener vi har vært gode. Vi har delt ut oppgaver som de 
forskjellige meldemene ønsket å gjøre noe som vi mener har bidratt til en god gruppedynamikk gjennom hele perioden.
Vi har hatt noen utfordringer med at alle skal bidra like mye i kodebasen med bakgrunn av at alle ikke har like my erfaring 
og da trenger lengre til på å gjennomføre noe som andre kunne gjort på kort tid. For å forbedre dette startet vi med møter der 
vi brukte parprogrammering slik at alle visste hvor lang vi haadde kommet og hva som hadde blitt gjort.  
Ting som vi kunne forbedret er at vi i starten kunne satt en mer tydelig plan på hvordan vi skulle fordele arbeidsoppgavene
med tanke på at alle måtte bidra med alt. 
* Det vi synes har fungert best er at vi har hatt god kommunikasjon som gjør det lettere å stille spørsmål til andre i gruppen og raskt få svar.
Vi har hadd møter jevnlig som gjør at vi alle har holdt seg oppdatert på hva som er "nytt". En annen ting som vi synes er 
spesielt god er at vi har hatt 
tydelige krav som har gjort det enkele å fullføre alle mvp. 
* Hvis vi skulle fortsette på prosjektet ville vi prøvd å fått det over til en app og grafisk vennesystem. 
* Vi har erfart er at god kommunikasjon, tydelige krav og jenvnlige møter har vært vikttigst for oss.
 
## Deloppgave 2: Krav
-	Liste med fullførte krav 

## Deloppgave 3: Kode 
-	Hvordan bygge prosjekter og kjøre: 
```bash 
$ mvn clean install 
$ mvn package 
$ java -jar target/Server.jar
$ java -jar target/Client.jar
```
-	For mac legg til ``` -XstartOnFirstThread``` som jvm argument.
-	For å kjøre test:
```bash 
$ mvn test
```

