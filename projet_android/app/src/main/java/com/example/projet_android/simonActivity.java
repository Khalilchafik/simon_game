//auteurs : Chafik khalil et Ouabel Abderrahim
package com.example.projet_android;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class simonActivity extends AppCompatActivity {
    // Définir des constantes pour les IDs des boutons de couleur
    private static final int COULEUR_ROUGE = R.id.color_pad_red;
    private static final int COULEUR_BLEU = R.id.color_pad_blue;
    private static final int COULEUR_VERT = R.id.color_pad_green;
    private static final int COULEUR_JAUNE = R.id.color_pad_yellow;

    // Liste pour stocker la séquence des couleurs
    private List<Integer> sequence;
    private int etapeActuelle;
    private int score;

    // SoundPool pour gérer les effets sonores
    private SoundPool soundPool;
    private int sonRouge, sonBleu, sonVert, sonJaune;

    // TextView pour afficher le score
    private TextView scoreTextView;

    private String nomJoueur;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simon);

        // Initialisation de SoundPool et chargement des sons
        soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
        sonRouge = soundPool.load(this, R.raw.son1, 1);
        sonBleu = soundPool.load(this, R.raw.son2, 1);
        sonVert = soundPool.load(this, R.raw.son3, 1);
        sonJaune = soundPool.load(this, R.raw.son4, 1);

        // Récupèrer le nom du joueur depuis l'intent
        nomJoueur = getIntent().getStringExtra("nomJoueur");

        // Initialisation des variables de jeu
        sequence = new ArrayList<>();
        etapeActuelle = 0;
        score = 0;
        scoreTextView = findViewById(R.id.score_label);

        // Configurer les boutons de couleur
        configurerBoutonsCouleur();

        // Démarrer le jeu
        demarrerJeu();
    }

    private void configurerBoutonsCouleur() {
        // Tableau des IDs des boutons de couleur
        int[] idsBoutons = {COULEUR_ROUGE, COULEUR_BLEU, COULEUR_VERT, COULEUR_JAUNE};
        for (int id : idsBoutons) {
            // Récupèrer chaque bouton par son ID
            Button bouton = findViewById(id);
            bouton.setAlpha(0.5f); // Définir une opacité initiale faible pour tous les boutons
            bouton.setOnClickListener(this::gererClicCouleur); // Définir le listener de clic pour chaque bouton
        }
    }

    private void gererClicCouleur(View view) {
        // Récupèrer l'ID de la couleur cliquée
        int couleurCliquee = view.getId();
        if (!sequence.isEmpty() && couleurCliquee == obtenirRessourceCouleurPourIndice(sequence.get(etapeActuelle))) {
            etapeActuelle++;
            if (etapeActuelle == sequence.size()) {
                score++;
                scoreTextView.setText("Score: " + score);
                ajouterProchaineCouleurASequence();
                etapeActuelle = 0;
            }
        } else {
            finJeu();
        }
        jouerSonPourCouleur(couleurCliquee);
    }

    private int obtenirRessourceCouleurPourIndice(int indice) {
        // Retourner l'ID du bouton correspondant à l'indice de la couleur
        switch (indice) {
            case 1: return COULEUR_ROUGE;
            case 2: return COULEUR_BLEU;
            case 3: return COULEUR_VERT;
            case 4: return COULEUR_JAUNE;
            default: return -1; // Gestion d'erreur
        }
    }

    private void demarrerJeu() {
        // Ajouter la première couleur à la séquence pour démarrer le jeu
        ajouterProchaineCouleurASequence();
    }

    private void ajouterProchaineCouleurASequence() {
        // Génèrer une couleur aléatoire et l'ajouter à la séquence
        Random random = new Random();
        int prochainIndiceCouleur = random.nextInt(4) + 1; // Génère 1 à 4
        sequence.add(prochainIndiceCouleur);
        afficherSequence();
    }

    private void afficherSequence() {
        // Afficher la séquence des couleurs avec un délai entre chaque surbrillance
        int delai = 0;
        for (int indiceCouleur : sequence) {
            final Button bouton = findViewById(obtenirRessourceCouleurPourIndice(indiceCouleur));
            bouton.postDelayed(() -> surlignerCouleur(bouton), 1000 * delai++);
        }
    }

    private void surlignerCouleur(Button bouton) {
        // Faire briller la couleur en augmentant l'opacité et joue le son correspondant
        bouton.setAlpha(1.0f);
        jouerSonPourCouleur(bouton.getId());
        bouton.postDelayed(() -> bouton.setAlpha(0.5f), 500); // Réinitialiser à une opacité plus faible après surbrillance
    }

    private void jouerSonPourCouleur(int couleurCliquee) {
        // Jouer le son correspondant à la couleur cliquée
        int idSon = obtenirIdSonPourCouleur(couleurCliquee);
        soundPool.play(idSon, 1, 1, 0, 0, 1);
    }

    private int obtenirIdSonPourCouleur(int couleurCliquee) {
        // Retourner l'ID du son correspondant à la couleur cliquée
        if (couleurCliquee == COULEUR_ROUGE) {
            return sonRouge;
        } else if (couleurCliquee == COULEUR_BLEU) {
            return sonBleu;
        } else if (couleurCliquee == COULEUR_VERT) {
            return sonVert;
        } else if (couleurCliquee == COULEUR_JAUNE) {
            return sonJaune;
        } else {
            return -1; // Son d'erreur
        }
    }

    private void finJeu() {
        // Terminer le jeu et lancer le son d'erreur
        scoreTextView.setText("Game Over! Final score: " + score);
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.error);
        mediaPlayer.setOnCompletionListener(mp -> {
            mp.release();
            retournerScoreAActivitePrincipale();
        });
        mediaPlayer.start();
    }

    private void retournerScoreAActivitePrincipale() {
        // Retourner le score et le nom du joueur à l'activité principale
        Intent resultIntent = new Intent();
        resultIntent.putExtra("score", score);
        resultIntent.putExtra("nomJoueur", nomJoueur);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        soundPool.release(); // Libérer les ressources de SoundPool
    }
}
