# Movie Review Sentiment Analyzer

_Sentiment Analysis_ е процесът по алгоритмично идентифициране и категоризиране на мнения, изразени в свободен текст, особено за да се определи дали отношението на автора към конкретна тема, продукт и т.н. е позитивно, негативно или неутрално.

_Machine Learning_ е особено нашумяло в наши дни направление в компютърните науки, което изучава класове от алгоритми, които "се учат" от данни. Такъв тип алгоритми работят, като изграждат модел от примерни входни данни и използват този модел, за да правят предсказания или взимат решения.

Sentiment analyzer-ът за филмови отзиви, автоматично определя степента на позитивност на даден отзив в свободен текст.

Например, алгоритъмът би определил отзива:

_"Dire disappointment: dull and unamusing freakshow"_

като твърдо негативен, докато отзивът:

_"Immersive ecstasy: energizing artwork!"_

ще се класифицира като еднозначно позитивен.

Данните, от които ще "учи" алгоритъмът, са множество от 8,529 филмови отзива (ревюта), за които отношението на автора е било оценено от човек по скала от 0 до 4 със следната семантика:

|  рейтинг| семантика |
|--|--|
| 0 | negative |
| 1 | somewhat negative |
| 2 | neutral |
| 3 | somewhat positive |
| 4 | positive |

Използваният data set е от сайта [Rotten Tomatoes](https://www.rottentomatoes.com/), използван наскоро за престижния [Кaggle machine learning competition](https://www.kaggle.com/c/sentiment-analysis-on-movie-reviews).

Данните са налични в текстовия файл [reviews.txt](https://github.com/fmi/java-course/tree/master/homeworks/02-movie-review-sentiment-analyzer/resources/reviews.txt), като всеки ред от файла започва с рейтинг, следван от интервал и текста на отзива, например:

```
4 The performances are an absolute joy .
```

Напълно е очаквано в подобен real-life data set да има typos, жаргонни или направо несъществуващи думи.

Има обаче едно множество от често срещани в свободен текст думи, които носят твърде малко семантика: определителни членове, местоимения, предлози, съюзи и т.н. Такива думи се наричат _stopwords_ и много алгоритми, свързани с обработка на естествен език (NLP, natural language processing), ги игнорират - т.е. премахват ги от съответните входни текстове, защото внасят "шум", т.е. намаляват качеството на резултата. Няма еднозначна дефиниция (или речник) коя дума е stopword в даден език. Алгоритъмът ползва списъка от 174 stopwords в английския език, записани по една на ред в текстовия файл [stopwords.txt](https://github.com/fmi/java-course/tree/master/homeworks/02-movie-review-sentiment-analyzer/resources/stopwords.txt), заимстван от сайта [ranks.nl](https://www.ranks.nl/stopwords).

## Алгоритъм

Обучение:

1.  Изчитат се отзивите в [reviews.txt](https://github.com/fmi/java-course/tree/master/homeworks/02-movie-review-sentiment-analyzer/resources/reviews.txt)
2.  Изчислява се sentiment score на всяка дума като средно аритметично (без закръгляване) на всички рейтинги, в които участва дадената дума. Дума е последователност от малки и главни латински букви и цифри с дължина поне един символ. Думите са case-insensitive, т.е. "Movie", "movie" и "movIE" се третират като една и съща дума. Един отзив се състои от думи, разделени с разделители: интервали, табулации и препинателни знаци - въобще всеки символ, който не е буква или цифра. Stopwords се игнорират, т.е. не се взимат под внимание.

Разпознаване:

1.  По даден текст на отзив се изчислява неговият sentiment score като средно аритметично (без закръгляване) на sentiment scores на всяка дума в отзива. Дефиницията на дума е същата като по-горе, и stopwords отново се игнорират. Игнорират се също всички (непознати) думи, за които алгоритъмът не е обучен, т.е. не се срещат нито веднъж в [reviews.txt](https://github.com/fmi/java-course/tree/master/homeworks/02-movie-review-sentiment-analyzer/resources/reviews.txt). Sentiment score на отзив, в който не се съдържа нито една дума с известен sentiment score (състои се само от непознати думи и/или stopwords), се приема за -1.0 (unknown).

Нашият data set от ревюта може да се разширява. Това допринася за допълнителна точност при меренето на sentiment във времето. При добавяне на нови ревюта и оценки, се преизчислява sentiment-a на думите от ревюто.
