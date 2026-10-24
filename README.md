# TP – Service web REST « Calculatrice » avec JAX-RS (Jersey) et Tomcat 10.1

**Nom / Prénom :** Hanen Akermi
**Groupe :** MPSRCC1
**Date :** 24/01/2026

## Sommaire

1. Présentation du projet
2. Partie A : Conception
3. Partie C : Premier service (somme)
4. Partie D : Compléter la calculatrice
5. Partie E : JSON
6. Partie F : Ressource `operations` (POST, GET, PUT, DELETE)
7. Questions de synthèse
8. Conclusion

---

## 1. Présentation du projet

Le projet est une application web Maven (`packaging = war`) qui expose une calculatrice sous forme de service REST avec **JAX-RS** (implémentation **Jersey 3.1.9**), déployée dans **Tomcat 10.1** (Jakarta EE 10).

| Élément | Valeur |
|---|---|
| JDK | 17 |
| Maven | 3.9+ |
| Serveur | Apache Tomcat 10.1.x |
| Artefact | `target/RestWebApp.war` |
| URL de base | `http://localhost:8082/RestWebApp/api` (Tomcat configuré sur le port **8082** sur ma machine ; 8080 par défaut) |

### Structure du projet

```
RestWebApp/
├── pom.xml
└── src/main/java/com/exemple/rest/
    ├── Calculatrice.java       (logique métier)
    ├── RestApplication.java    (@ApplicationPath("/api"))
    ├── CalculRest.java         (ressource /calcul)
    ├── Resultat.java           (objet JSON des calculs détaillés)
    ├── Operation.java          (objet de l'historique)
    └── OperationRest.java      (ressource /operations)
```
<img width="1858" height="728" alt="Capture d&#39;écran 2026-10-23 234043" src="https://github.com/user-attachments/assets/00aa5cba-3b77-425c-8044-a7f9c7e720f0" />

### Construction et déploiement

```bash
mvn clean package        # produit target/RestWebApp.war
```

Déploiement : copie du `.war` dans `TOMCAT_HOME/webapps` ou configuration Tomcat d'IntelliJ (Run > Edit Configurations, onglet Deployment, contexte d'application `/RestWebApp`).
<img width="1236" height="1017" alt="Capture d&#39;écran 2026-10-23 234351" src="https://github.com/user-attachments/assets/a3a0ef08-24f7-4538-81ce-1de760695e1b" />

---

## 2. Partie A : Conception du service

**Q1. Pourquoi les calculs directs utilisent-ils GET et non POST ?**
Un calcul direct est une opération de **lecture** : le client demande un résultat et rien n'est créé ni modifié sur le serveur. GET est le verbe prévu pour cela (sûr, idempotent, pouvant être mis en cache, et l'URL peut être testée directement dans un navigateur). POST sert à créer une ressource ou à déclencher un effet de bord.

**Q2. Comment s'appelle la partie située après `?` dans `/calcul/somme?a=2&b=3` ?**
C'est la **chaîne de requête** (*query string*), composée des **paramètres de requête** (*query parameters*) `a=2` et `b=3`.

**Q3. Différence entre `/operations` et `/operations/3` ?**
`/operations` désigne la **collection** de toutes les opérations (GET la liste, POST en crée une). `/operations/3` désigne **une ressource précise** de cette collection : l'opération d'identifiant 3 (GET la lit, PUT la modifie, DELETE la supprime).

**Q4. Statut HTTP pour `/operations/99` inexistante ?**
**404 Not Found** (ressource non trouvée).

---

## 3. Partie C : Premier service REST (somme)

### Code

`Calculatrice.java`, `RestApplication.java` et `CalculRest.java` (méthode `somme`) sont ceux de l'énoncé (voir le dossier `src`).

**B.1 Création du projet (IntelliJ, générateur Jakarta EE)**

<img width="1242" height="977" alt="Capture d&#39;écran 2026-10-23 234150" src="https://github.com/user-attachments/assets/f91da4af-8f56-4e41-96aa-131c09e32f9a" />



**B.3 Fichier `pom.xml`**
<img width="1143" height="896" alt="Capture d&#39;écran 2026-10-23 234229" src="https://github.com/user-attachments/assets/ab703e3b-8a19-41f8-b51a-fc70ec87ef43" />


**C.1 Classe `Calculatrice`**
<img width="1222" height="847" alt="Capture d&#39;écran 2026-10-23 234246" src="https://github.com/user-attachments/assets/3815acb6-d9ca-445c-92f2-fad1e24fb88a" />



**C.2 Classe `RestApplication`**

<img width="1277" height="566" alt="Capture d&#39;écran 2026-10-23 234259" src="https://github.com/user-attachments/assets/6143dfbe-f8ce-4cb2-9384-163e0cd62b89" />


**C.4 Construction : fichier `target/RestWebApp.war`**

<img width="737" height="770" alt="Capture d&#39;écran 2026-10-23 234327" src="https://github.com/user-attachments/assets/c6e53a61-fdf1-4a43-845a-c0a081dcbb99" />


**C.5 Configuration Tomcat**
<img width="997" height="987" alt="Capture d&#39;écran 2026-10-24 111333" src="https://github.com/user-attachments/assets/1e0b5e0c-718b-44eb-820e-174c689b4657" />



> Tomcat 10.1.61 est lancé sur le port 8082 : les URL des captures utilisent donc `localhost:8082`.

### Tests de l'étape C.7

| Outil | Requête | Résultat attendu | Résultat obtenu |
|---|---|---|---|
| Navigateur | `GET /RestWebApp/api/calcul/somme?a=2&b=3` | `5` | `5` (voir capture) |
| curl | `curl.exe -i "http://localhost:8082/RestWebApp/api/calcul/somme?a=2&b=3"` | `HTTP/1.1 200`, `Content-Type: text/plain`, corps `5` | `HTTP/1.1 200`, `Content-Type: text/plain`, corps `5` (voir capture) |
| Postman | GET + Send | `5`, `200 OK` | `5`, `200 OK` (voir capture) |

**Navigateur**

<img width="1888" height="573" alt="Capture d&#39;écran 2026-10-23 235042" src="https://github.com/user-attachments/assets/24ea9f04-d7bd-4bdb-835b-97e7594034cf" />




**curl**

<img width="1462" height="402" alt="Capture d&#39;écran 2026-10-23 235251" src="https://github.com/user-attachments/assets/ed1d8bd8-55be-4dcd-9957-5237fb37e687" />

**Postman (GET)**

<img width="1247" height="847" alt="Capture d&#39;écran 2026-10-20 145718" src="https://github.com/user-attachments/assets/95dfc596-5038-4fc7-a43c-8937976fb18e" />


**Question C.1. Avec POST au lieu de GET, quel statut ? Pourquoi ?**
Le statut obtenu est **405 Method Not Allowed**. L'URI `/calcul/somme` existe, mais la méthode Java n'est annotée que par `@GET` : aucune méthode ne répond au verbe POST pour cette URI. Jersey renvoie donc 405 (et non 404, car la ressource existe).

<img width="1028" height="865" alt="Capture d&#39;écran 2026-10-20 145709" src="https://github.com/user-attachments/assets/4bcfe2b4-b73c-45a6-a8b1-00a0c061f126" />


---

## 4. Partie D : Compléter la calculatrice

### D.1 Soustraction et produit

```java
@GET
@Path("/soustraction")
@Produces(MediaType.TEXT_PLAIN)
public Response soustraction(@QueryParam("a") int a, @QueryParam("b") int b) {
    return Response.ok(calculService.soustraction(a, b)).build();
}

@GET
@Path("/produit")
@Produces(MediaType.TEXT_PLAIN)
public Response produit(@QueryParam("a") int a, @QueryParam("b") int b) {
    return Response.ok(calculService.produit(a, b)).build();
}
```

| Requête | Attendu | Obtenu |
|---|---|---|
| `GET /calcul/soustraction?a=10&b=4` | `6` | `6` (200) |
| `GET /calcul/produit?a=6&b=7` | `42` | `42` (200) |

![curl soustraction]<img width="1262" height="276" alt="Capture d&#39;écran 2026-10-24 000644" src="https://github.com/user-attachments/assets/e788685e-055b-4c95-8e62-58cf932de5a8" />


![curl produit]<img width="1397" height="245" alt="Capture d&#39;écran 2026-10-24 000724" src="https://github.com/user-attachments/assets/ce4bccdc-13e8-494f-839d-5eea888f10c6" />


### D.2 Division et gestion d'erreur

Méthode `division` de l'énoncé (renvoie 400 si `b == 0`).

| Requête | Statut attendu | Corps attendu | Obtenu |
|---|---|---|---|
| `GET /calcul/division?a=7&b=2` | 200 | `3.5` | 200, `3.5` (voir capture) |
| `GET /calcul/division?a=7&b=0` | 400 | `Erreur : division par 0 impossible` | 400, `Erreur : division par 0 impossible` (voir capture) |

![curl division 7/2]<img width="1322" height="357" alt="Capture d&#39;écran 2026-10-24 000420" src="https://github.com/user-attachments/assets/5c014cb0-4f19-4bd3-9cab-279bd4472417" />


![curl division 7/0]<img width="1308" height="272" alt="Capture d&#39;écran 2026-10-24 000442" src="https://github.com/user-attachments/assets/3b28f87b-4171-456c-b09b-90605d617fb6" />


### D.3 Observations

| Requête | Statut | Corps |
|---|---|---|
| `GET /calcul/somme?a=2` (b absent) | 200 | `2` |
| `GET /calcul/somme?a=abc&b=3` | 404 | page d'erreur HTML par défaut de Tomcat (« Non trouvé ») |

![curl b absent]<img width="1348" height="285" alt="Capture d&#39;écran 2026-10-24 000756" src="https://github.com/user-attachments/assets/9d20718c-bd69-4571-b79e-27993c58b6fc" />


![curl a non entier]<img width="1533" height="365" alt="Capture d&#39;écran 2026-10-24 000843" src="https://github.com/user-attachments/assets/a2e278f3-68ce-4bb4-8eab-dafef113899d" />


**Question D.1. Quelle valeur JAX-RS donne-t-il à un paramètre entier absent ?**
La **valeur par défaut du type Java** : `0` pour un `int`. Ici `b = 0`, donc `somme?a=2` renvoie `2`. (Avec un type objet comme `Integer`, la valeur serait `null`. On peut aussi imposer une valeur avec `@DefaultValue`.)

**Question D.2. Statut quand le paramètre n'est pas un entier ? La méthode a-t-elle été exécutée ?**
Le statut est **404 Not Found** : pour `@QueryParam` et `@PathParam`, la spécification JAX-RS renvoie 404 quand la conversion de la chaîne `"abc"` en `int` échoue. La méthode `somme` **n'est pas exécutée** : l'erreur survient pendant l'injection des paramètres, avant l'appel de la méthode.

### D.4 `@PathParam`

```java
@GET
@Path("/produit/{a}/{b}")
@Produces(MediaType.TEXT_PLAIN)
public Response produitChemin(@PathParam("a") int a, @PathParam("b") int b) {
    return Response.ok(calculService.produit(a, b)).build();
}
```

Test : `GET /calcul/produit/6/7` renvoie `42` (statut 200).

![curl produit chemin]<img width="1265" height="282" alt="Capture d&#39;écran 2026-10-24 000920" src="https://github.com/user-attachments/assets/fe9936ec-d871-4b68-bc76-4f35a71143c0" />


**Question D.3. Différence entre `@QueryParam` et `@PathParam` ? Laquelle pour `/operations/{id}` ?**
- `@QueryParam` lit un paramètre placé **après le `?`** (`?a=2&b=3`). Il est généralement **optionnel** et sert à filtrer, paramétrer ou trier.
- `@PathParam` lit une valeur située **dans le chemin** de l'URI (`/produit/6/7`). Il fait partie de l'identité de la ressource et est **obligatoire** pour que l'URI corresponde.

Pour l'identifiant dans `/operations/{id}` on utilise **`@PathParam("id")`**, car l'identifiant désigne la ressource elle-même.

---

## 5. Partie E : Échanger des données en JSON

`Resultat.java` et la méthode `sommeDetail` sont ceux de l'énoncé.

```bash
curl -i "http://localhost:8080/RestWebApp/api/calcul/somme/detail?a=2&b=3"
```

Résultat attendu : `200`, `Content-Type: application/json`, corps `{"a":2,"b":3,"operation":"somme","resultat":5.0}`. Obtenu : statut 200, `Content-Type: application/json`, corps identique.

![curl somme detail]<img width="1233" height="246" alt="Capture d&#39;écran 2026-10-24 002323" src="https://github.com/user-attachments/assets/71c4c609-147e-4faa-b275-33e585e67c97" />


**Question E.1. Qui a effectué la conversion ? D'où viennent les noms ?**
La conversion objet Java → JSON est faite automatiquement par **Jersey**, grâce à la dépendance `jersey-media-json-binding` (qui s'appuie sur JSON-B / Yasson). Les noms `a`, `b`, `operation`, `resultat` viennent des **propriétés de la classe `Resultat`**, déduites de ses **accesseurs** (`getA()`, `getB()`, `getOperation()`, `getResultat()`). Le constructeur sans argument et les setters servent à la conversion inverse (JSON → objet).

### E.3 En-tête `Accept`

| Requête | Statut attendu | Obtenu |
|---|---|---|
| `curl -i -H "Accept: application/json" ".../somme/detail?a=2&b=3"` | 200 | 200 |
| `curl -i -H "Accept: application/xml" ".../somme/detail?a=2&b=3"` | 406 | 406 (Not Acceptable) |

![Accept JSON : 200]<img width="1440" height="293" alt="Capture d&#39;écran 2026-10-24 002515" src="https://github.com/user-attachments/assets/1cff97b3-afda-4da8-85ef-281213f7113c" />


![Accept XML : 406]<img width="1435" height="400" alt="Capture d&#39;écran 2026-10-24 002527" src="https://github.com/user-attachments/assets/7059da03-b3b0-4041-b9b5-24b7a333d00f" />


**Question E.2. Pourquoi la seconde requête est-elle refusée ?**
Le client demande du XML (`Accept: application/xml`) alors que la méthode ne produit que du JSON (`@Produces(MediaType.APPLICATION_JSON)`). Le serveur ne peut pas satisfaire le format demandé et répond **406 Not Acceptable**.

### E.4 Exercice : `divisionDetail`

```java
@GET
@Path("/division/detail")
@Produces(MediaType.APPLICATION_JSON)
public Response divisionDetail(@QueryParam("a") int a, @QueryParam("b") int b) {
    if (b == 0) {
        return Response.status(Response.Status.BAD_REQUEST).build();
    }
    Resultat resultat = new Resultat("division", a, b, calculService.division(a, b));
    return Response.ok(resultat).build();
}
```

| Requête | Statut | Corps |
|---|---|---|
| `GET /calcul/division/detail?a=7&b=2` | 200 | `{"a":7,"b":2,"operation":"division","resultat":3.5}` |
| `GET /calcul/division/detail?a=7&b=0` | 400 | aucun corps défini dans la méthode : page d'erreur HTML par défaut de Tomcat |

![curl division detail 7/2]<img width="1072" height="235" alt="Capture d&#39;écran 2026-10-24 003258" src="https://github.com/user-attachments/assets/5d7b3078-10e0-4985-af7e-a38437aaf009" />


![curl division detail 7/0]<img width="1352" height="373" alt="Capture d&#39;écran 2026-10-24 003304" src="https://github.com/user-attachments/assets/d4ca6ea1-bec7-4482-a3a5-31a824b00d32" />


---

## 6. Partie F : Ressource `operations`

Les données sont stockées en mémoire (`ConcurrentHashMap` + `AtomicInteger`, attributs `static`).

### F.1 et F.2 Classes `Operation` et `OperationRest`

`Operation.java` (id, operateur, a, b, resultat) et `OperationRest.java` (POST, GET liste, GET par id) sont ceux de l'énoncé.

### F.3 Tester la création et la lecture

Après avoir reconstruit et redéployé l'application, les requêtes suivantes sont envoyées dans l'ordre.

**Création avec curl, sous Linux, macOS ou Git Bash :**

```bash
curl -i -X POST -H "Content-Type: application/json" -d '{"operateur":"somme","a":8,"b":2}' "http://localhost:8080/RestWebApp/api/operations"
```

**Création avec curl, dans l'invite de commandes Windows (cmd)** : les guillemets internes doivent être précédés d'une barre oblique inverse :

```bat
curl -i -X POST -H "Content-Type: application/json" -d "{\"operateur\":\"somme\",\"a\":8,\"b\":2}" "http://localhost:8080/RestWebApp/api/operations"
```

L'option `-X` choisit le verbe HTTP et l'option `-d` fournit le corps de la requête.

**Création avec curl sous PowerShell (méthode utilisée pour les tests)** : pour éviter les problèmes de guillemets, les corps JSON sont d'abord écrits dans des fichiers, puis envoyés avec `--data @fichier` :

```powershell
Set-Content body1.json '{"operateur":"somme","a":8,"b":2}'
Set-Content body2.json '{"operateur":"division","a":7,"b":2}'
Set-Content body6.json '{"operateur":"division","a":7,"b":0}'
Set-Content body7.json '{"operateur":"racine","a":9,"b":0}'
$u = "http://localhost:8082/RestWebApp/api/operations"
curl.exe -i -X POST -H "Content-Type: application/json" --data "@body1.json" $u
```

![Préparation des corps JSON]<img width="1307" height="242" alt="Capture d&#39;écran 2026-10-24 010102" src="https://github.com/user-attachments/assets/d3d27e7e-8dd0-4587-a3d8-c8f1096e55aa" />

**Création avec Postman :**

1. Choisir le verbe `POST` et l'URL `http://localhost:8080/RestWebApp/api/operations`.
2. Ouvrir l'onglet **Body**, sélectionner **raw**, puis le type **JSON**.
3. Saisir le corps `{"operateur":"somme","a":8,"b":2}` et cliquer sur **Send**.
<img width="912" height="857" alt="Capture d&#39;écran 2026-10-24 005147" src="https://github.com/user-attachments/assets/89b61c7b-8f5a-45cb-9b6c-738b5735618c" />

**Série de tests** (toutes les URI sont à préfixer par `http://localhost:8082/RestWebApp/api` sur ma machine) :

| N° | Requête | Corps envoyé | Statut attendu | Réponse attendue | Statut obtenu | Réponse obtenue |
|---|---|---|---|---|---|---|
| 1 | `POST /operations` | `{"operateur":"somme","a":8,"b":2}` | 201 | `{"a":8,"b":2,"id":1,"operateur":"somme","resultat":10.0}` et un en-tête `Location` se terminant par `/operations/1` | 201 | `{"a":8,"b":2,"id":1,"operateur":"somme","resultat":10.0}` et `Location: http://localhost:8082/RestWebApp/api/operations/1` |
| 2 | `POST /operations` | `{"operateur":"division","a":7,"b":2}` | 201 | Opération d'identifiant 2, de résultat 3.5 | 201 | `{"a":7,"b":2,"id":2,"operateur":"division","resultat":3.5}` et `Location: …/operations/2` |
| 3 | `GET /operations` | Aucun | 200 | Tableau JSON contenant les deux opérations | 200 | `[{"a":8,"b":2,"id":1,"operateur":"somme","resultat":10.0},{"a":7,"b":2,"id":2,"operateur":"division","resultat":3.5}]` |
| 4 | `GET /operations/2` | Aucun | 200 | L'opération d'identifiant 2 | | |
| 5 | `GET /operations/99` | Aucun | 404 | Corps vide | | |
| 6 | `POST /operations` | `{"operateur":"division","a":7,"b":0}` | 400 | `{"erreur":"Calcul impossible"}` | | |
| 7 | `POST /operations` | `{"operateur":"racine","a":9,"b":0}` | 400 | `{"erreur":"Calcul impossible"}` | 400 | `{"erreur":"Calcul impossible"}` |

**Captures des tests (curl)**

Test 1 : création de l'opération somme (201 + en-tête `Location`, corps de la réponse en haut de la capture du test 2) :

![Test 1](captures/f3_test1.png)

Test 2 : création de l'opération division (201, id 2, résultat 3.5) :



<img width="1327" height="305" alt="Capture d&#39;écran 2026-10-24 010126" src="https://github.com/user-attachments/assets/d0699509-007e-430e-af18-c693241e0c0f" />

Test 3 : liste des opérations (200, tableau de 2 opérations) :

<img width="1216" height="227" alt="Capture d&#39;écran 2026-10-24 010134" src="https://github.com/user-attachments/assets/3858e16f-7648-4038-892f-7a8e799bf6aa" />

Test 7 : opérateur inconnu (400) :

![Test 7](captures/f3_test7.png)

Tests 4, 5 et 6 : *(captures à ajouter)*

**Complément : lecture de l'opération 1** (`GET /operations/1`, 200) :

![GET operations/1](captures/f3_get_operation_1.png)

**Création avec Postman** : `POST /operations` avec le corps `{"operateur":"somme","a":8,"b":2}` renvoie `201 Created`. L'identifiant obtenu est 3, car deux opérations avaient déjà été créées avec curl :

![Postman POST]<img width="912" height="857" alt="Capture d&#39;écran 2026-10-24 005147" src="https://github.com/user-attachments/assets/8cc3fa87-bed1-4504-94c5-3a02b9ddaf3a" />

Liste des opérations après la création Postman (3 opérations) :

![GET operations après Postman](captures/f3_get_operations_apres_postman.png)

### F.4 PUT : modifier

Imports : `jakarta.ws.rs.PUT`.

```java
@PUT
@Path("/{id}")
@Consumes(MediaType.APPLICATION_JSON)
public Response modifier(@PathParam("id") int id, Operation op) {
    if (!OPERATIONS.containsKey(id)) {
        return Response.status(Response.Status.NOT_FOUND).build();
    }
    if (!calculer(op)) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"erreur\":\"Calcul impossible\"}")
                .build();
    }
    op.setId(id);
    OPERATIONS.put(id, op);
    return Response.ok(op).build();
}
```

**Tests F.4**

| Requête | Corps | Statut attendu | Statut obtenu | Corps obtenu |
|---|---|---|---|---|
| `PUT /operations/1` | `{"operateur":"produit","a":8,"b":2}` | 200, résultat `16.0` | | |
| `GET /operations/1` | – | 200, résultat `16.0` | | |
| `PUT /operations/99` | `{"operateur":"somme","a":1,"b":1}` | 404 | | |
| `PUT /operations/1` | `{"operateur":"division","a":1,"b":0}` | 400 | | |

*(captures F.4)*

### F.5 DELETE : supprimer

Imports : `jakarta.ws.rs.DELETE`.

```java
@DELETE
@Path("/{id}")
public Response supprimer(@PathParam("id") int id) {
    Operation supprimee = OPERATIONS.remove(id);
    if (supprimee == null) {
        return Response.status(Response.Status.NOT_FOUND).build();
    }
    return Response.noContent().build();
}
```

**Tests F.5**

| Requête | Statut attendu | Statut obtenu | Corps obtenu |
|---|---|---|---|
| `DELETE /operations/1` | 204 | | |
| `GET /operations/1` | 404 | | |
| `DELETE /operations/1` | 404 | | |

*(captures F.5)*

---

---

## 7. Questions de synthèse

### 1. Tableau de l'étape A.1 avec les codes de statut

| Verbe | URI | Rôle | Codes de statut possibles |
|---|---|---|---|
| GET | `/calcul/somme?a=2&b=3` | Retourne a + b | 200 ; 404 si un paramètre n'est pas un entier |
| GET | `/calcul/soustraction?a=2&b=3` | Retourne a − b | 200 ; 404 si paramètre invalide |
| GET | `/calcul/produit?a=2&b=3` | Retourne a × b | 200 ; 404 si paramètre invalide |
| GET | `/calcul/division?a=6&b=3` | Retourne a ÷ b | 200 ; 400 si b = 0 ; 404 si paramètre invalide |
| POST | `/operations` | Crée une opération | 201 ; 400 (calcul impossible ou JSON invalide) ; 415 si le corps n'est pas du JSON |
| GET | `/operations` | Liste toutes les opérations | 200 |
| GET | `/operations/{id}` | Retourne l'opération id | 200 ; 404 si id inconnu |
| PUT | `/operations/{id}` | Modifie l'opération id | 200 ; 400 (calcul impossible) ; 404 si id inconnu ; 415 si le corps n'est pas du JSON |
| DELETE | `/operations/{id}` | Supprime l'opération id | 204 ; 404 si id inconnu |

Codes transverses possibles : 405 (verbe non autorisé sur l'URI), 406 (format `Accept` non supporté), 500 (erreur interne non gérée).

### 2. Annotations JAX-RS utilisées

| Annotation | Rôle |
|---|---|
| `@ApplicationPath` | Définit le préfixe commun (`/api`) de toutes les URI et active JAX-RS. |
| `@Path` | Associe une classe ou une méthode à un fragment d'URI (avec ou sans `{variable}`). |
| `@GET`, `@POST`, `@PUT`, `@DELETE` | Indiquent le verbe HTTP auquel répond la méthode. |
| `@Produces` | Indique le(s) type(s) MIME de la réponse (`text/plain`, `application/json`). |
| `@Consumes` | Indique le type MIME du corps de requête accepté (`application/json`). |
| `@QueryParam` | Injecte un paramètre de requête (après le `?`). |
| `@PathParam` | Injecte une valeur située dans le chemin de l'URI. |
| `@Context` | Injecte un objet du contexte (ici `UriInfo` pour construire l'en-tête `Location`). |

### 3. Différence entre JAX-RS et Jersey

**JAX-RS** (Jakarta RESTful Web Services) est une **spécification** : elle définit l'API (annotations, classes `Response`, `Application`…) mais ne fournit aucun code exécutable. **Jersey** est une **implémentation** de cette spécification (l'implémentation de référence), qui exécute réellement le routage des requêtes, l'injection des paramètres et la conversion JSON. D'autres implémentations existent (RESTEasy, Apache CXF).

### 4. Rôle de Tomcat ; pourquoi un `.war` et non un `.jar` exécutable ?

Tomcat est un **conteneur de servlets** (serveur d'applications web) : il écoute sur le réseau (port 8080), reçoit les requêtes HTTP, les transmet à l'application (ici la servlet de Jersey) et renvoie les réponses. Il gère aussi le cycle de vie de l'application.

Le projet produit un **`.war`** (*Web ARchive*) car c'est le format standard d'une application web destinée à être **déployée dans un conteneur existant** : il contient les classes, les bibliothèques (`WEB-INF/lib`) et la configuration web. Le projet n'a pas de méthode `main` ni de serveur embarqué : ce n'est pas un programme autonome, d'où l'inutilité d'un `.jar` exécutable.

### 5. Stateless et historique des opérations

Le principe *stateless* concerne l'**état de session/conversation du client** : chaque requête doit contenir toutes les informations nécessaires pour être comprise (identifiant dans l'URI, corps, paramètres), sans que le serveur s'appuie sur le souvenir des requêtes précédentes de ce client (pas de session).

L'historique est un **état de la ressource** (des données de l'application, partagées par tous les clients), et non un état du client. Le serveur ne retient rien sur *qui* fait la requête ni sur ce qu'il a fait avant : `GET /operations/2` donne le même résultat quel que soit le client et l'historique de ses appels. Stocker des données de l'application est donc compatible avec le principe stateless (en pratique, elles seraient placées dans une base de données).

### 6. POST répété et PUT répété

- **Deux `POST /operations` identiques** : chaque appel **crée une nouvelle ressource** (ids 1 puis 2, deux réponses 201 avec des `Location` différents). L'effet se **cumule** : POST n'est **pas idempotent**.
- **Deux `PUT /operations/1` identiques** : le second appel remplace l'opération par le même contenu. L'état final du serveur est **identique** à celui obtenu après un seul appel (200 les deux fois). PUT est **idempotent**.

---

## 8. Conclusion

Ce TP m'a permis de concevoir une API REST (choix des URI et des verbes), de créer un projet Maven de type `war`, d'écrire des ressources avec les annotations JAX-RS, de les déployer dans Tomcat 10.1, d'échanger des données en JSON et de tester le service avec le navigateur, curl et Postman en interprétant les codes de statut (200, 201, 204, 400, 404, 405, 406).
