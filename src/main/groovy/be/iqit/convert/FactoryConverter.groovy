package be.iqit.convert

import rx.Observable

/**
 * Created by dvanroeyen on 02/12/15.
 */
class FactoryConverter extends AbstractConverter {

    Map<Class, Converter> map = [:]
    Converter defaultConverter

    FactoryConverter withDefaultConverter(Converter defaultConverter) {
        this.defaultConverter = defaultConverter
        return this
    }

    FactoryConverter withConverter(Class from, Class to, Converter converter) {
        map.put(getKey(from, to), converter)
    }

    @Override
    def <I, O> Observable<O> doConvert(I object, Class<O> clazz) {
        Converter converter = findConverter(object?.getClass(), clazz)
        if(!converter) {
            throw new IllegalArgumentException("No converter for ${object.getClass()} to ${clazz}")
        }
        return converter.convert(object, clazz)
    }

    public Converter findConverter(Class from, Class to) {
        Converter converter =  map.get(getKey(from, to))
        if(!converter) {
            converter = defaultConverter
        }
        return converter
    }

    private String getKey(Class from, Class to) {
        return from.getName()+'/'+to.getName()
    }
}
