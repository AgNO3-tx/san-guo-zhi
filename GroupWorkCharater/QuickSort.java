public class QuickSort {

        public static<T extends Comparable<? super T>> T[] quickSort(T[] arr){

            if(arr == null || arr.length <= 1){
                return arr;
            }

            quickSort(arr, 0, arr.length-1);
            return arr;
        }

        private  static<T extends Comparable<? super T>> void quickSort(T[] arr,int start, int end){
            if(start >= end){return;}

            int i = start, j = end;
            T pivot = arr[start];
            while(i < j){
                while((i < j) && (arr[j].compareTo(pivot) >= 0)){
                    j--;
                }
                while((i < j) && (arr[i].compareTo(pivot) <= 0)){
                    i++;
                }
                if(i < j){
                    swapElements(arr,i,j);
                }
            }
            swapElements(arr,start,j);
            quickSort(arr,start,j-1);
            quickSort(arr,j+1,end);
        }

    public static<T extends  Comparable<? super T>> void swapElements(T[] a, int i, int j) {     //交换两个元素
        T temp;
        temp = a[i];
        a[i] = a[j];
        a[j] = temp;
    }
}
