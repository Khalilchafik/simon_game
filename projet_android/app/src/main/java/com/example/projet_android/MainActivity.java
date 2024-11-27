//auteurs : Chafik khalil 
package com.example.projet_android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    public static final String NOM_PREFS = "SimonPrefs";
    private TextView score1TextView, score2TextView, score3TextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        effacerScores(); // Effacer les scores au début
        setContentView(R.layout.activity_main); // Définit le layout de l'activité

        // Initialiser les TextViews pour afficher les scores
        score1TextView = findViewById(R.id.score1);
        score2TextView = findViewById(R.id.score2);
        score3TextView = findViewById(R.id.score3);

        // Configurer le bouton "Jouer" pour demander le nom du joueur et démarrer le jeu
        Button boutonJouer = findViewById(R.id.play_button);
        boutonJouer.setOnClickListener(v -> demanderNomJoueur());

        // Configurer le bouton "Quitter" pour fermer l'application
        Button boutonQuitter = findViewById(R.id.quit_button);
        boutonQuitter.setOnClickListener(v -> finish());

        // Afficher les meilleurs scores
        afficherMeilleursScores();
    }

    // Demander le nom du joueur
    private void demanderNomJoueur() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Entrez le nom du joueur");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT); // Définit le type d'entrée comme du texte
        builder.setView(input); // Ajouter le champ de texte à la boîte de dialogue

        builder.setPositiveButton("OK", (dialog, which) -> {
            String nomJoueur = input.getText().toString().isEmpty() ? "Anonyme" : input.getText().toString();
            demarrerJeuSimon(nomJoueur); // Démarrer le jeu avec le nom du joueur
        });
        builder.setNegativeButton("Annuler", (dialog, which) -> dialog.cancel()); // Annuler la boîte de dialogue
        builder.show();
    }

    // Démarrer le jeu Simon avec le nom du joueur
    private void demarrerJeuSimon(String nomJoueur) {
        Intent intent = new Intent(this, simonActivity.class);
        intent.putExtra("nomJoueur", nomJoueur); // Passer le nom du joueur à l'activité Simon
        startActivityForResult(intent, 1); // Démarrer l'activité Simon et attendre un résultat
    }

    // Reçeption des résultats de l'activité Simon
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            int score = data.getIntExtra("score", 0); // Récupèrer le score
            String nomJoueur = data.getStringExtra("nomJoueur"); // Récupèrer le nom du joueur
            mettreAJourMeilleursScores(score, nomJoueur); // Met à jour les meilleurs scores
        }
    }

    // Mettre à jour les meilleurs scores si nécessaire
    private void mettreAJourMeilleursScores(int nouveauScore, String nomJoueur) {
        SharedPreferences prefs = getSharedPreferences(NOM_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        // Récupèrer les scores et noms actuels
        int score1 = prefs.getInt("score1", -1);
        String nom1 = prefs.getString("nom1", "");
        int score2 = prefs.getInt("score2", -1);
        String nom2 = prefs.getString("nom2", "");
        int score3 = prefs.getInt("score3", -1);
        String nom3 = prefs.getString("nom3", "");

        // Initialiser les tableaux pour les scores et les noms
        int[] scores = {score1, score2, score3};
        String[] noms = {nom1, nom2, nom3};

        // Ajouter le nouveau score au tableau et trier les scores en ordre décroissant
        for (int i = 0; i < scores.length; i++) {
            if (nouveauScore > scores[i]) {
                for (int j = scores.length - 1; j > i; j--) {
                    scores[j] = scores[j - 1];
                    noms[j] = noms[j - 1];
                }
                scores[i] = nouveauScore;
                noms[i] = nomJoueur;
                break;
            }
        }

        // Enregistrer les nouveaux scores et noms dans les préférences partagées
        editor.putInt("score1", scores[0]);
        editor.putString("nom1", noms[0]);
        editor.putInt("score2", scores[1]);
        editor.putString("nom2", noms[1]);
        editor.putInt("score3", scores[2]);
        editor.putString("nom3", noms[2]);

        editor.apply(); // Appliquer les modifications
        afficherMeilleursScores(); // Afficher les meilleurs scores mis à jour
    }

    // Afficher les meilleurs scores
    private void afficherMeilleursScores() {
        SharedPreferences prefs = getSharedPreferences(NOM_PREFS, MODE_PRIVATE);
        score1TextView.setText("1. " + prefs.getString("nom1", "No Name") + " : " + prefs.getInt("score1", 0));
        score2TextView.setText("2. " + prefs.getString("nom2", "No Name") + " : " + prefs.getInt("score2", 0));
        score3TextView.setText("3. " + prefs.getString("nom3", "No Name") + " : " + prefs.getInt("score3", 0));
    }

    // Effacer les scores enregistrés
    private void effacerScores() {
        SharedPreferences prefs = getSharedPreferences(NOM_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear(); // Efface toutes les préférences
        editor.apply(); // Applique les modifications
    }
}
