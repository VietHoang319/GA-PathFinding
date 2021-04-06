import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;
import java.io.BufferedReader;
import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TSP_GA 
{
    // int[i] là mảng 1 chiều có kiểu dữ liệu là int, với i là số phần từ của mảng.
    // int[i][j] là mảng 2 chiều có kiểu dữ liệu là int, với i là số hàng của ma trận, j là số cột của ma trận.
    public int max = 10000;
    // Khởi tạo mảng 2 chiều khoảng cách, lưu lại khoảng cách của đề bài.
    public int kc[][] = null;
    // Tạo một đối tượng random
    public Random rd = new Random();
    // Số cá thể.
    public static int n = 50;
    // Khởi tạo mảng 2 chiều solution, lưu lại chuỗi mã hóa các cá thể.
    int[][] solution = new int[n][];
    // Khởi tạo mảng 1 chiều fitness, lưu độ thích nghi của cá thể.
    int[] fitness = new int[n];
    // float[] limit = new float[n];

    // Hàm đọc file ma trận .txt chuyển thành mảng 2 chiều.
    public boolean loadData(String filepath)
    {
        // Khai báo biến đường dẫn file
        Path path = Paths.get(filepath);
        Charset cs = Charset.forName("US-ASCII");

        // Đọc dữ liệu từ file
        try(BufferedReader reader = Files.newBufferedReader(path, cs))
        {
            // Khai báo biến dòng
            String line = null;
            // Khai báo biến đếm
            int count = 0;
            // Nếu số dòng chưa hết thì vẫn tiếp tục đọc.
            while ((line = reader.readLine()) != null)
            {
                // Khai báo biến w là các từ trong dòng
                String w[] = line.split(" ");

                // Nếu w có độ dài bằng 1 thì mảng kc đều có số đỉnh = giá trị ở dòng đầu tiên trong file txt.
                if (w.length == 1)
                {
                    kc = new int[Integer.parseInt(w[0])][Integer.parseInt(w[0])];
                }
                // Nếu không thì thêm các giá trị vào mảng kc.
                else
                {
                    for (int i = 0; i < w.length; i++)
                    {
                        kc[count][i] = Integer.parseInt(w[i]);
                    }
                    count++;
                }
            }
        }
        // Báo lỗi.
        catch(Exception ex)
        {
            System.out.println(ex.getMessage());
            return false;
        }

        // In mảng kc.
        for(int i = 0; i < kc.length; i++)
        {
            for(int j = 0; j < kc.length; j++)
            {
                System.out.print(kc[i][j]+ " ");
            }
            System.out.println();
        }
        return true;
    }

    // Hàm khởi tạo quần thể, với mỗi cá thể là tập hợp 5 thành phố.
    public void Initialization()
    {
        for (int i = 0; i < n; i++)
        {
            int sz = Array.getLength(kc);
            solution[i] = new int[sz+1];
            solution[i][0] = 0;
            solution[i][sz] = 0;
            //Vector check = new Vector<>();
            for (int j = 1; j < sz; j++)
            {
                // Trả về ma trận ngẫu nhiên trong phạm vi 5 vì có 5 thành phố.
                solution[i][j] = 1 + rd.nextInt(sz-1);
            }
        }
    }   

    // Hàm tính độ thích nghi của cá thể trong quần thể.
    public void Evaluation()
    {
        for (int i = 0; i < n; i++)
        {
            int sz = Array.getLength(kc);
            // Gán fitness ban đầu bằng 0.
            fitness[i] = 0;
            for (int j = 0; j < sz; j++)
            {
                // Cộng khoảng cách giữa các thành phố với nhau.
                fitness[i] += kc[solution[i][j]][solution[i][j + 1]];
            }
            for (int j = 0; j < sz; j++)
            {
                for (int t = j + 1; t < sz; t++)
                {
                    // Nếu các gene trùng nhau thì fitness cộng thêm 10000 để không trùng.
                    if (solution[i][j] == solution[i][t])
                    {
                        fitness[i] += 100000;
                    }
                }
            }
        }
    }

    // Hàm chọn lọc cá thể.
    public void Selection()
    {
        // for(int i = 0; i < n; i++)
        // {
        //     limit[i] = (1/fitness[i])*100;
        //     if (limit[i] < 1)
        //     {
        //         solution[i] = solution[rd.nextInt(n)].clone();
        //         limit[i] = (1/fitness[i])*100;
        //     }
        //     Arrays.sort(limit);
        //     System.out.println(fitness[i]);
        //     System.out.println(limit[i]);
        // }
        // Tạo một bản sao của fitness.
        int temp[] = fitness.clone();
        // Sắp xếp các phần tử của mảng.
        Arrays.sort(temp);
        int nguong = temp[n * 80 / 100];
		for (int i = 0; i < n; i++){
			if (fitness[i] > nguong){
				solution[i] = solution[rd.nextInt(n)].clone();
			}
		}
        // Lấy ngưỡng ở 80%
        // float limit = temp[n * 80 / 100];
        // for (int i = 0; i < n; i++)
        // {
        //     // So sánh fitness của cá thể với ngưỡng 80%.
        //     if (fitness[i] > limit)
        //     {
        //         // Cá thể có fitness lớn hơn ngưỡng thì cá thể đó được lấy ngẫu nhiên để tạo cá thể mới.
        //         solution[i] = solution[rd.nextInt(n)].clone();
        //     }
        // }
    }

    // Hàm lai ghép cá thể con = nửa cá thể cha + nửa cá thể mẹ.
    public void Crossover()
    {
        for (int i = 0; i < 20; i++)
        {
            // Lấy ngẫu nhiên một cá thể cha.
            int dad = rd.nextInt(n);
            // Lấy ngẫu nhiêu một cá thể mẹ.
            int mom = rd.nextInt(n);
            for (int j = 0; j < solution[dad].length; j++)
            {
                if (rd.nextInt(2) == 1)
                {
                    int temp = solution[dad][j];
                    solution[dad][j] = solution[mom][j];
                    solution[mom][j] = temp;
                }
            }
        }
    }

    // Hàm đột biến.
    public void Mutation()
    {
        int sz = Array.getLength(kc);
        // Chọn ngẫu nhiên một cá thể.
        int index = rd.nextInt(n);
        // Chọn ngẫu nhiên một gen.
        int bit = rd.nextInt(5);
        // Gây đột biến gen đã chọn.
        if (bit != 0)
        {
            solution[index][bit] = rd.nextInt(sz);
        }
        if (bit == 0)
        {
            return;
        }
    }

    // Hàm in kết quả.
    public void Print()
    {
        int temp[] = fitness.clone();
        // Sắp xếp fitness theo thứ tự tăng dần.
        Arrays.sort(temp);
        // Lấy fitness nhỏ nhất.
        float best = temp[0];
        System.out.print(best + ": ");
        for (int i = 0; i < n; i++)
        {
            // Nếu fitness của cá thể bằng với "best" thì in ra cái chuỗi của cá thể đó.
            if (fitness[i] == best)
            {
                for (int j = 0; j < solution[i].length; j++)
                {
                    System.out.print(solution[i][j] + ", ");
                }
                System.out.println();
                break;
            }
        }
    }

    public static void main(String[] args) 
    {
        TSP_GA x = new TSP_GA();
        x.loadData("E:\\Project\\Mobile Robot\\JAVA_CODE\\test.txt");
        x.Initialization();
        Scanner sc = new Scanner(System.in);
        System.out.print("Nhập số vòng đời quá trình di truyền diễn ra: ");
        int g = sc.nextInt();
        for (int i = 0; i < g; i++)
        {
            x.Evaluation();
            x.Print();
            x.Selection();
            x.Crossover();
            x.Mutation();
        }
        sc.close();
    }
}