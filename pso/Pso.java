package swarm;

import java.util.Random;
import java.util.Scanner;
/**
 *
 * @author Infinity
 */
public class Pso {
    private final int jmlHari;
    private final int jmlPartikel;
    private int popSize;
    private int maxIterasi;
    private final double v[][];
    private double x[][];
    private final double Pbest[][];
    private double Gbest[];
    
    private final int jmlIndeksMakanan;
    private final double kgProtein = 12.5;
    private final double kgKarbohidrat = 65;
    private final double kgLemak = 22.5;
    
    private double k = 0.6, r1 = 0.1, r2 = 0.3, 
            w = 0.5, c1 = 1, c2 = 1;
    private double vmin = 0, vmax = 0;
    
    private final String namaKompMakanan[] = 
    {"Cumi-cumi", "Ikan Belida", "Dendeng", "Mujair", "Telur Ayam Lokal", 
        "Beras", "Jagung", "Tepung Terigu", "Kentang", "Beras Merah", 
        "Usus Ayam", "Kacang Kedelai", "Keju", "Alpukat", "Mentega"};
    
    // kalori, protein, karbohidrat, lemak
    private final double kandGizi[][] = 
    {{75,16.1,0,0},{120,16.5,0,0},{357,41.1,0,0},{89,18.7,0,0},{198,13,0,0},
     {130,0,28,0},{345,0,74,0},{364,0,76,0},{77,0,17,0},{111,0,23,0},
     {473,0,0,26.3},{381,0,0,16.7},{402,0,0,33},{160,0,0,15},{717,0,0,81}};
    
    private int umur;
    private int tinggiBadan;
    private int beratBadan;
    private double BMR;
    
    public Pso(){
//        Scanner sc = new Scanner(System.in);
//        System.out.print("Masukkan nilai popsize: ");
//        this.popSize = sc.nextInt();
//        System.out.print("Masukkan jumlah hari: ");
//        this.jmlHari = sc.nextInt();
//        System.out.print("Masukkan jumlah iterasi: ");
//        this.maxIterasi = sc.nextInt();
//        
//        System.out.print("Masukan umur: ");
//        this.umur = sc.nextInt();
//        System.out.print("Masukan tinggi badan: ");
//        this.tinggiBadan = sc.nextInt();
//        System.out.print("Masukan berat badan: ");
//        this.beratBadan = sc.nextInt();
        
        
        this.popSize = 3;
        this.jmlHari = 12;
        this.umur = 21;
        this.tinggiBadan = 170;
        this.beratBadan = 65;
        this.maxIterasi = 250;
        this.jmlIndeksMakanan = 15;
        this.jmlPartikel = 3*3*this.jmlHari;
        this.v = new double[this.popSize][this.jmlPartikel];
        this.x = new double[this.popSize][this.jmlPartikel+1];
        this.Pbest = new double[this.popSize][this.jmlPartikel+1];
        this.Gbest = new double[this.jmlPartikel+1];
        
        
        
    }
    
    public void setKasus(int u, int tb, int bb){
        this.umur = u;
        this.tinggiBadan = tb;
        this.beratBadan = bb;
    }
    
    public void hitungBMR(){
        double bmr = ((66 + (13.7 * this.beratBadan) + (5 * this.tinggiBadan) 
                - (6.8 * this.umur)) * 1.375) * this.jmlHari;
        this.BMR = bmr;
    }
    
    public static void main(String[] args) {
        Pso test = new Pso();
        test.prosesPSO();
    }
    
    public void inisialisasi(){
        inisialisasiKecepatan();
        inisialisasiPartikel();
    }
    
    public void inisialisasiKecepatan(){
        for (int i = 0; i < this.popSize; i++) {
            for (int j = 0; j < this.jmlPartikel; j++) {
                this.v[i][j] = 0;
            }
        }
    }

    //sistem asli
    public void inisialisasiPartikel(){
        Random rand = new Random();
        int counter = 0;
        for (int i = 0; i < this.popSize; i++) {
            for (int m = 0; m < this.jmlHari; m++) {
                for (int n = 0; n < 3; n++) {
                    // protein
                    this.x[i][counter++] = rand.nextInt(5);
                    // karbohidrat
                    this.x[i][counter++] = rand.nextInt(5) + 5;
                    // lemak
                    this.x[i][counter++] = rand.nextInt(5) + 10;
                }
            }
            // hitung fitness partikel indeks ke-i
            this.x[i][this.x[i].length - 1] = hitungFitness(this.x[i]);
            counter = 0;
        }
    }
    
    public double hitungFitness(double partikel[]){
        int counter = 0, counter2 = 0;
        double protein = 0, karbo = 0, lemak = 0, 
                kalori = 0, totKandGizi,
                probProtein, probKarbo, probLemak,
                selisihProtein, selisihKarbo, selisihLemak,
                selisihKalori, selisihKandungan, fitness;
        for (int m = 0; m < this.jmlHari; m++) {
            // hitung kalori
            for (int o = 0; o < 9; o++) {
                kalori += this.kandGizi[(int)partikel[counter2++]][0];
            }
            
            // hitung kandungan gizi protein, karbohidrat, lemak
            for (int n = 0; n < 3; n++) {
                // protein
                protein += this.kandGizi[(int)partikel[counter++]][1];
                // karbohidrat
                karbo += this.kandGizi[(int)partikel[counter++]][2];
                // lemak
                lemak += this.kandGizi[(int)partikel[counter++]][3];
            }
        }
        
        totKandGizi = protein+karbo+lemak;
        
        probProtein = protein/totKandGizi*100;
        probKarbo = karbo/totKandGizi*100;
        probLemak = lemak/totKandGizi*100;
        
        selisihProtein = Math.abs(this.kgProtein-probProtein);
        selisihKarbo = Math.abs(this.kgKarbohidrat-probKarbo);
        selisihLemak = Math.abs(this.kgLemak-probLemak);
        
        selisihKalori = Math.abs(this.BMR-kalori);
        selisihKandungan = selisihProtein+selisihKarbo+selisihLemak;
        
        fitness = 1/(1+selisihKalori+selisihKandungan);
        
        return fitness;
    }
    
    // untuk pencocokan manualisasi
    public void inisialisasiPartikel2(){
        Random rand = new Random();
        int counter = 0;
        int arr[][] = 
                    {{2,7,10,2,8,14,3,9,14,4,7,13,4,7,11,2,8,13},
                    {1,6,13,2,7,13,2,5,12,0,5,13,1,9,13,3,9,14},
                    {3,9,10,4,6,12,1,8,14,1,8,11,1,8,12,0,5,13}};
        for (int i = 0; i < this.popSize; i++) {
            for (int m = 0; m < this.jmlHari; m++) {
                for (int n = 0; n < 3; n++) {
                    // protein
                    this.x[i][counter] = arr[i][counter++];
                    // karbohidrat
                    this.x[i][counter] = arr[i][counter++];
                    // lemak
                    this.x[i][counter] = arr[i][counter++];
                }
            }
            // hitung fitness partikel indek ke i
            this.x[i][this.x[i].length-1] = hitungFitness(this.x[i]);
            counter = 0;
        }
        updatePBest();
        updateGBest();
    }
    
    public void updatePBest(){
        for (int i = 0; i < this.popSize; i++) {
            if(this.x[i][this.x[i].length-1] > this.Pbest[i][this.x[i].length-1]){
                this.Pbest[i] = this.x[i];
            }
        }
    }
    
    public void updateGBest(){
        double max = 0.0;
        for (int i = 0; i < this.popSize; i++) {
            if(max < this.Pbest[i][this.x[i].length-1]){
                max = this.Pbest[i][this.x[i].length-1];
                this.Gbest = this.Pbest[i];
            }
        }
    }
    
    public void velocityClamping(){
        this.vmax = this.k * (5 - 1)/2;
        this.vmin = -vmax;
    }
    
    public void updateKecepatan(){
        for (int i = 0; i < this.popSize; i++) {
            for (int j = 0; j < this.jmlPartikel; j++) {
                double tempr1 = Math.random();
                double tempr2 = Math.random();
                this.v[i][j] = this.w*this.v[i][j] + this.c1*tempr1*
                        (this.Pbest[i][j]-this.x[i][j])+this.c2*tempr2*
                        (this.Gbest[j]-this.x[i][j]);
                if(this.v[i][j] > this.vmax){
                    this.v[i][j] = this.vmax;
                }
                if(this.v[i][j] < this.vmin){
                    this.v[i][j] = this.vmin;
                }
            }
        }
    }
    
    public void updatePosisi(){
        double tempX[][] = new double[this.popSize][this.jmlPartikel+1];
        for (int i = 0; i < this.popSize; i++) {
            for (int j = 0; j < this.jmlPartikel; j++) {
                tempX[i][j] = this.x[i][j] + this.v[i][j];
                tempX[i][j] = Math.round(tempX[i][j]);
                
                
                // cek jika protein kurang dari 0 dan lebih dari 4
                if(j % 3 == 0 && tempX[i][j] < 0){
                    tempX[i][j] = 0;
                }else if(j % 3 == 0 && tempX[i][j] > 4){
                    tempX[i][j] = 4;
                }
                
                // cek jika karbohidrat kurang dari 5 dan lebih dari 9                
                if(j % 3 == 1 && tempX[i][j] < 5){
                    tempX[i][j] = 5;
                }else if(j % 3 == 1 && tempX[i][j] > 9){
                    tempX[i][j] = 9;
                }
                
                // cek jika lemak kurang dari 10 dan lebih dari 14
                if(j % 3 == 2 && tempX[i][j] < 10){
                    tempX[i][j] = 10;
                }else if(j % 3 == 2 && tempX[i][j] > 14){
                    tempX[i][j] = 14;
                }
            }
            tempX[i][this.x[i].length-1] = hitungFitness(tempX[i]);
        }
        this.x = tempX;
    }
    
    public void prosesPSO(){
//// pengujian jumlah iterasi
//        hitungBMR();
//        velocityClamping();
//        int miterasi = 50;
//        for (int x = 0; x < 10; x++) {
//            this.maxIterasi = miterasi;
//            inisialisasi();
//            System.out.printf("Max iterasi %d fitness: ", this.maxIterasi);
//            for (int i = 0; i < this.maxIterasi; i++) {
//                updateKecepatan();
//                updatePosisi();
//                updatePBest();
//                updateGBest();
//                
//            }
//            miterasi += 50;
//            printFitness();
//        }
    
// proses pengujian c1 dan c2        
//        hitungBMR();
//        velocityClamping();
//        double tempc1[] = {0.5, 0.5, 1, 1, 1, 1.5, 1.5};
//        double tempc2[] = {0.5, 1, 0.5, 1, 1.5, 1, 1.5};
//
//        for (int x = 0; x < 7; x++) {
//            this.c1 = tempc1[x];
//            this.c2 = tempc2[x];
//            inisialisasi();
//            System.out.printf("c1: %.1f c2: %.1f fitness: ", this.c1, this.c2);
//            for (int i = 0; i < this.maxIterasi; i++) {
//                //System.out.print("Iterasi "+(i+1)+" ");
//                updateKecepatan();
//                updatePosisi();
//                updatePBest();
//                updateGBest();
//                
//            }
//            printFitness();
//        }
        
        
        
        
        
        inisialisasi();
//        System.out.println("Iterasi 1");
//        System.out.println("Inisialisasi Kecepatan Awal");
//        printKecepatan();
//        System.out.println("Inisialisasi Posisi Awal");
//        printPartikel();
//        System.out.println("Inisialisasi Pbest dan GBest");
//        printPbest();
//        printGbest();
        for (int i = 0; i < this.maxIterasi; i++) {
            System.out.print("Iterasi "+(i+1)+" ");
  //          System.out.println("Update Kecepatan");
            updateKecepatan();
  //          printKecepatan();
  //          System.out.println("Update Posisi");
            updatePosisi();
  //          printPartikel();
            //System.out.println("Update Pbest dan Gbest");
            updatePBest();
            updateGBest();
            //printPbest();
  //          printGbest();
            printFitness();
            //System.out.println("");
        }
        decoding();
    }
    
    public void printPartikel(){
        System.out.println("Partikel: ");
        for (int i = 0; i < this.popSize; i++) {
            for (int j = 0; j < this.jmlPartikel+1; j++) {
                System.out.print(this.x[i][j]+" ");
            }
            System.out.println();
        }
    }
    public void printKecepatan(){
        System.out.println("Kecepatan: ");
        for (int i = 0; i < this.popSize; i++) {
            for (int j = 0; j < this.jmlPartikel; j++) {
                System.out.print(this.v[i][j]+" ");
            }
            System.out.println();
        }
    }
    public void printPbest(){
        System.out.println("PBest: ");
        for (int i = 0; i < this.popSize; i++) {
            for (int j = 0; j < this.jmlPartikel+1; j++) {
                System.out.print(this.Pbest[i][j]+" ");
            }
            System.out.println();
        }
    }
    public void printGbest(){
        System.out.println("GBest: ");
        for (int j = 0; j < this.jmlPartikel+1; j++) {
            System.out.print(this.Gbest[j]+" ");
        }
        System.out.println();
    }    
    public void printFitness(){
        System.out.println("Fitness: "+this.Gbest[this.Gbest.length-1]);
    }
    public void decoding(){
        int counter = 0;
        System.out.println("Hasil Komposisi Makanan Atlet Basket: ");
        for (int m = 0; m < this.jmlHari; m++) {
            System.out.println("Hari:\t"+(m+1));
            for (int n = 0; n < 3; n++) {
                switch (n) {
                    case 0:
                        System.out.print("Pagi:\t");
                        break;
                    case 1:
                        System.out.print("Siang:\t");
                        break;
                    default:
                        System.out.print("Malam:\t");
                        break;
                }
                System.out.print(this.namaKompMakanan[(int) this.Gbest[counter++]] + ", ");
                System.out.print(this.namaKompMakanan[(int) this.Gbest[counter++]] + ", ");
                System.out.print(this.namaKompMakanan[(int) this.Gbest[counter++]]);
                System.out.println();
            }
            System.out.println();
        }
    }
}