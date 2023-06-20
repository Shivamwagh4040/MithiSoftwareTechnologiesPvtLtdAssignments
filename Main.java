import java.io.*;
import java.util.*;

class BookIndexer 
{
    private Set<String> excludeWords;
    private Map<String, Set<Integer>> wordIndex;

    public BookIndexer(String excludeWordsFilePath) 
    {
        excludeWords = loadExcludeWords(excludeWordsFilePath);
        wordIndex = new TreeMap<>();
    }

    public void indexPages(String[] pageFilePaths) 
    {
        for (int i = 0; i < pageFilePaths.length; i++) 
        {
            String pagePath = pageFilePaths[i];
            indexPage(pagePath, i + 1);
        }
    }

    private void indexPage(String pagePath, int pageNumber) 
    {
        try (BufferedReader reader = new BufferedReader(new FileReader(pagePath))) 
        {
            String line;
            while ((line = reader.readLine()) != null) 
            {
                String[] words = line.split("\\s+");
                for (String word : words) 
                {
                    if (!excludeWords.contains(word.toLowerCase())) 
                    {
                        wordIndex.computeIfAbsent(word.toLowerCase(), k -> new HashSet<>()).add(pageNumber);
                    }
                }
            }
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }

    public void saveIndex(String indexPath) 
    {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(indexPath))) 
        {
            for (Map.Entry<String, Set<Integer>> entry : wordIndex.entrySet()) 
            {
                String word = entry.getKey();
                Set<Integer> pages = entry.getValue();
                writer.write(word + " : ");
                writer.write(pagesToString(pages));
                writer.newLine();
            }
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }

    private String pagesToString(Set<Integer> pages) 
    {
        StringBuilder sb = new StringBuilder();
        for (int page : pages) 
        {
            sb.append(page).append(",");
        }
        sb.deleteCharAt(sb.length() - 1); // Remove the last comma
        return sb.toString();
    }

    private Set<String> loadExcludeWords(String excludeWordsFilePath) 
    {
        Set<String> excludeWordsSet = new HashSet<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(excludeWordsFilePath))) 
        {
            String line;
            while ((line = reader.readLine()) != null) 
            {
                excludeWordsSet.add(line.trim().toLowerCase());
            }
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
        return excludeWordsSet;
    }
}

public class Main 
{
    public static void main(String[] args) 
    {
        String[] pageFilePaths = {"C:/Users/admin/Downloads/Page1.txt", "C:/Users/admin/Downloads/Page2.txt", "C:/Users/admin/Downloads/Page3.txt"};
        String excludeWordsFilePath = "C:/Users/admin/Downloads/exclude-words.txt";
        String indexPath = "C:/Users/admin/Downloads/index.txt";

        BookIndexer indexer = new BookIndexer(excludeWordsFilePath);
        indexer.indexPages(pageFilePaths);
        indexer.saveIndex(indexPath);

        System.out.println("Index created successfully!");
    }
}
