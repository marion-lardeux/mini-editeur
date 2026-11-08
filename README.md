# Mini-éditeur de texte

TP2 d'OMD (ESIR2, 2026-2027) : conception orientée objet d'un mini-éditeur de texte en Java (interface graphique Swing), réalisé « en spirale » en deux versions successives.

**Auteurs** : Marion Lardeux et Aurore Le Manac'h

## Présentation

Le texte est contenu dans un **buffer**. Une **sélection** (deux bornes, début et fin ; un simple
curseur quand elles sont égales) délimite la zone sur laquelle agissent les commandes de copie, de
coupe et de collage, qui passent par un **presse-papier**.

| Version | Ajoute |
|---|---|
| **V1** | Saisie, déplacement du curseur, sélection, copier / couper / coller, retour arrière |
| **V2** | Défaire / refaire (historique non borné), enregistrement et rejeu des actions de l'utilisateur |

Les deux versions sont **autonomes** : chacune a son propre dossier et son propre `pom.xml`, et se
construit et se lance indépendamment de l'autre.

## Organisation du dépôt

```
mini-editeur/
├── README.md                      ce fichier
├── docs/                          dossier de conception : modèles UML de chaque version
│   ├── v1/uml/                    classes.puml, classes.png
│   └── v2/uml/                    classes.puml, classes.png
├── v1/                            VERSION 1
│   ├── pom.xml
│   └── src/
│       ├── main/java/editor/
│       │   ├── Main.java          assemblage des objets et ouverture de la fenêtre
│       │   ├── model/             Buffer, Selection, Clipboard, EditorEngine,
│       │   │                      EditorObserver, EditorReader
│       │   ├── controller/        EditorController
│       │   └── view/              SwingView, KeyboardHandler
│       └── test/java/editor/model/EditorEngineTest.java
└── v2/                            VERSION 2 (reprend la V1 et l'étend)
    ├── pom.xml
    └── src/
        ├── main/java/editor/
        │   ├── Main.java
        │   ├── command/           Command, CommandManager                  (nouveau)
        │   ├── model/             les classes de la V1 + EditorMemento     (nouveau)
        │   ├── controller/        EditorController (+ défaire, refaire, enregistrer, rejouer)
        │   └── view/              SwingView, KeyboardHandler (+ raccourcis de la V2)
        └── test/java/editor/command/CommandManagerTest.java
```

Le code est organisé selon l'architecture **MVC** :

| Couche | Paquet | Rôle |
|---|---|---|
| Modèle | `model` | Données et règles d'édition, **sans aucune dépendance à Swing** |
| Vue | `view` | Affichage du texte et de la sélection, lecture du clavier |
| Contrôleur | `controller` | Traduit les actions de l'utilisateur en appels au modèle |
| *(V2)* Commandes | `command` | Représentation des actions sous forme d'objets : historique et script |

Les dépendances vont dans un seul sens, sans cycle : `model` n'importe aucun autre paquet, `command`
dépend de `model`, `controller` de `command` et `model`, `view` de `controller` et `model`.

## Version 1 : édition de base

### Raccourcis clavier

| Action | Raccourci |
|---|---|
| Saisir du texte | frappe d'un caractère (Entrée et Tab inclus) |
| Déplacer le curseur | `←` / `→` |
| Sélectionner | `Maj+←` / `Maj+→` |
| Copier / Couper / Coller | `Ctrl+C` / `Ctrl+X` / `Ctrl+V` (`Cmd` sous macOS) |
| Supprimer | `Retour arrière` (⌫) : la sélection, ou le caractère avant le curseur |

### Choix de comportement

- Copier ou couper avec une sélection vide : sans effet, le presse-papier est inchangé.
- Coller avec un presse-papier vide : sans effet.
- Coller ou saisir avec une sélection non vide : la sélection est remplacée.
- Après un collage ou une saisie : sélection vide, curseur à la fin du texte inséré.
- Après une coupe : sélection vide à l'endroit où se trouvait le texte.
- Invariant garanti par `EditorEngine` : `0 ≤ début ≤ fin ≤ longueur du texte`. Les bornes sont
  ramenées dans `[0, longueur]`.
- La sélection mémorise son **point fixe** (là où elle a commencé). `Maj+→` / `Maj+←` déplacent
  l'autre extrémité : appuyer dans le sens inverse réduit la sélection, et si on dépasse le point
  fixe, elle se retourne.
- Une flèche seule, quand une sélection existe, la replie en curseur : au début avec `←`, à la fin
  avec `→`.
- Copier ne modifie ni le texte ni la sélection : les observateurs ne sont pas notifiés.

### Patrons de conception

| Patron | Problème résolu | Où |
|---|---|---|
| **MVC** (architectural) | Tester l'édition sans interface graphique, changer d'IHM sans toucher au modèle | paquets `model`, `view`, `controller` |
| **Observer** | La vue doit se rafraîchir quand le modèle change, sans que le modèle la connaisse | `EditorObserver`, `EditorEngine.notifyObservers()`, `SwingView.update()` |
| **Façade** | Une action coordonne `Buffer`, `Selection` et `Clipboard`, respecte l'invariant et notifie la vue : le contrôleur ne doit pas avoir à le faire | `EditorEngine` |

`EditorReader` donne aux vues un accès en **lecture seule** à l'état de l'éditeur : une vue peut
l'afficher sans pouvoir le modifier.

## Version 2 : défaire/refaire et enregistrement

La V2 reprend la V1 sans en changer l'architecture. Le modèle gagne seulement `save()` et
`restore()` (dans `EditorEngine`), et les actions du contrôleur passent désormais par un
`CommandManager`.

### Raccourcis ajoutés

| Action | Raccourci |
|---|---|
| Défaire | `Ctrl+Z` |
| Refaire | `Ctrl+Y` ou `Ctrl+Maj+Z` |
| Démarrer / arrêter l'enregistrement | `Ctrl+R` (même touche pour démarrer puis arrêter) |
| Rejouer le script enregistré | `Ctrl+P` |

### Choix de comportement

**Défaire / refaire**

- Défaire restaure le **texte et la sélection** d'avant l'action. Le **presse-papier n'est pas
  restauré**.
- Sont défaisables : la saisie, le retour arrière, couper et coller. Ne le sont pas : copier et les
  déplacements du curseur ou de la sélection.
- Une action qui ne change pas le texte (par exemple couper sans sélection) n'est pas empilée.
- L'historique est **non borné** : on peut revenir jusqu'au début.
- Une nouvelle action défaisable après un « défaire » vide la pile du « refaire ».
- Chaque caractère saisi constitue une étape de « défaire ».

**Enregistrement / rejeu**

- `Ctrl+R` démarre l'enregistrement (le script précédent est effacé), un second `Ctrl+R` l'arrête.
- Le script contient les actions d'édition : saisie, curseur, sélection, copier / couper / coller,
  retour arrière. Défaire et refaire n'y sont pas enregistrés.
- Le script est conservé **en mémoire** pour la durée de la session.
- Le rejeu est refusé pendant un enregistrement, et sans effet si le script est vide.
- Les actions rejouées **ne sont pas ré-enregistrées**, et passent par l'historique : chacune peut
  être défaite individuellement.

### Patrons de conception ajoutés

| Patron | Problème résolu | Où |
|---|---|---|
| **Command** | Une action doit devenir un objet pour être mémorisée dans un script et rejouée | interface `command.Command` ; `CommandManager.execute()` |
| **Memento** | Défaire exige de restaurer l'état exact (texte et sélection) | `EditorMemento`, `EditorEngine.save()` / `restore()` |

- Les commandes sont créées par `EditorController` sous forme de références de méthodes
  (`engine::cut`, `() -> engine.insert(s)`) : `Command` est une interface fonctionnelle, il n'y a
  pas de classe concrète par action.
- Les mémentos sont conservés par `CommandManager`, **pas par les commandes** : une commande ne
  contient que son action, ce qui permet de la rejouer autant de fois que nécessaire.
- `EditorMemento` reste dans le paquet `model` : ses accesseurs ne sont visibles que dans ce
  paquet, donc `CommandManager` peut le stocker sans pouvoir l'ouvrir.

## Prérequis, lancement et tests

**Prérequis** : JDK 17 ou plus récent, et Maven 3.6.3 ou plus récent.

```
java -version
mvn -v
```

Chaque version se construit depuis son propre dossier. Exemple pour la V1 :

```
cd v1
mvn test                                   # lance les tests JUnit
mvn package                                # construit target/mini-editeur-1.0.jar
java -jar target/mini-editeur-1.0.jar      # lance l'éditeur
```

Pour la V2 : `cd v2`, puis les mêmes commandes avec `target/mini-editeur-2.0.jar`.
Pour repartir d'un build propre : `mvn clean package`.

### Tests

| Version | Fichier | Nombre de tests | Ce qui est vérifié |
|---|---|---|---|
| V1 | `EditorEngineTest` | 12 | saisie, bornes du curseur, sélection, retour arrière, copier / couper / coller et leurs cas limites, notification des observateurs |
| V2 | `CommandManagerTest` | 10 | défaire / refaire, enregistrement et rejeu |

Le modèle et le gestionnaire de commandes se testent **sans lancer l'interface graphique** : c'est
l'un des bénéfices de la séparation MVC.
