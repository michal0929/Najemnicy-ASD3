package zadnajemnik;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.PrimitiveIterator.OfInt;
import java.util.Stack;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class ZadNajemnik {

  private final Zasoby zasoby;
  private final Zolnierz[] zolnierze;


  public static void main(String[] args) {
    runDynamicAlg("in3d.txt", "Out.txt");
 
  }
  
  public ZadNajemnik(Zasoby zasoby, Zolnierz[] zolnierze) {
    this.zasoby = zasoby;
    this.zolnierze = zolnierze;
  }
  
  private static void runDynamicAlg(String in, String out) {
    Path inputPath = Paths.get(in);
    try {
      if (Files.notExists(inputPath)) {
        Files.createFile(inputPath);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    try (Stream<String> lineStream = Files.lines(inputPath, StandardCharsets.UTF_8)) {
        
      // Wczytywanie danych
      OfInt intIterator = lineStream
          .flatMap(Pattern.compile("\\p{Blank}")::splitAsStream)//zamiana lini stringa na słowa 
          .flatMapToInt(word -> IntStream.of(Integer.parseInt(word)))//zamiana słowa na inta 
          .iterator();//

      int prowiant = intIterator.nextInt();
      int rozrywka = intIterator.nextInt();
      Zasoby zasoby = new Zasoby(prowiant, rozrywka);
     
      int n = intIterator.nextInt();
      Zolnierz[] zolnierze = new Zolnierz[n];
      int sila = 0;
      for (int i = 0; i < n; i++) {
        sila = intIterator.nextInt();
        prowiant = intIterator.nextInt();
        rozrywka = intIterator.nextInt();
        zolnierze[i] = new Zolnierz(new Zasoby(prowiant, rozrywka), sila);
      }

      Stack<Integer> stack = dynamicAlg(zasoby, zolnierze);
        PrintWriter plikWy = new PrintWriter(out);
        plikWy.println(stack.pop() + "\n");
      while (stack.size() > 0) {
       plikWy.print(stack.pop() + " ");
      }
      plikWy.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
 
  private static Stack<Integer> dynamicAlg(Zasoby zasoby, Zolnierz[] zolnierze) {

    int[][][] results = new int[zolnierze.length + 1][zasoby.getprowiant() + 1][zasoby.getrozrywka() + 1];

    for (int zolnierzIndex = 1; zolnierzIndex < results.length; zolnierzIndex++) {
      for (int prowiant = 1; prowiant < results[zolnierzIndex].length; prowiant++) {
        for (int rozrywka = 1; rozrywka < results[zolnierzIndex][prowiant].length; rozrywka++) {
          int testprowiant = prowiant - zolnierze[zolnierzIndex - 1].getprowiant();
          int testrozrywka = rozrywka - zolnierze[zolnierzIndex - 1].getrozrywka();
          if (testprowiant < 0 || testrozrywka < 0) {
            results[zolnierzIndex][prowiant][rozrywka] = results[zolnierzIndex - 1][prowiant][rozrywka];
          } else {
            results[zolnierzIndex][prowiant][rozrywka] = Math.max(
                results[zolnierzIndex - 1][prowiant][rozrywka],
                zolnierze[zolnierzIndex - 1].getSila() + results[zolnierzIndex - 1][testprowiant][testrozrywka]);
          }
        }
      }
    }

    int xprowiant = results[0].length - 1;
    int xrozrywka = results[0][0].length - 1;
    int zolnierzIndex = results.length - 1;

    int sila = results[zolnierzIndex][xprowiant][xrozrywka];
    Stack<Integer> stack = new Stack<>();

    while (zolnierzIndex > 0) {
      if (results[zolnierzIndex][xprowiant][xrozrywka] != results[zolnierzIndex - 1][xprowiant][xrozrywka]) {
        xprowiant -= zolnierze[zolnierzIndex - 1].getprowiant();
        xrozrywka -= zolnierze[zolnierzIndex - 1].getrozrywka();
        stack.push(zolnierzIndex);
      }
      --zolnierzIndex;
    }
    stack.push(sila); 
    return stack;
  }

  private static class Zasoby {

    int prowiant;
    int rozrywka;

    public Zasoby(int prowiant, int rozrywka) {
      this.prowiant = prowiant;
      this.rozrywka = rozrywka;
    }

    public int getprowiant() {
      return prowiant;
    }

    public void setprowiant(int prowiant) {
      this.prowiant = prowiant;
    }

    public int getrozrywka() {
      return rozrywka;
    }

    public void setrozrywka(int rozrywka) {
      this.rozrywka = rozrywka;
    }
  }

  private static class Zolnierz {

    Zasoby wymagania;
    int sila;

    public Zolnierz(Zasoby wymagania, int sila) {
      this.wymagania = wymagania;
      this.sila = sila;
    }

    public int getprowiant() {
      return wymagania.getprowiant();
    }

    public void setprowiant(int prowiant) {
      wymagania.setprowiant(prowiant);
    }

    public int getrozrywka() {
      return wymagania.getrozrywka();
    }

    public void setrozrywka(int rozrywka) {
      wymagania.setrozrywka(rozrywka);
    }

    public int getSila() {
      return sila;
    }

    public void setSila(int sila) {
      this.sila = sila;
    }
  }
    
}
