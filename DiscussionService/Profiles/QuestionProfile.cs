using AutoMapper;
using DiscussionService.Dtos;
using DiscussionService.Models;

namespace DiscussionService.Profiles
{
    public class QuestionProfile : Profile
    {
        public QuestionProfile()
        {
            CreateMap<CreateQuestionDto, Question>();
            CreateMap<Question, GetQuestionDto>()
                .ForMember((dest) => dest.Upvotes, opt => opt.MapFrom(src => src.Upvoters.Count));
            CreateMap<Question, GetQuestionsDto>()
                .ForMember((dest) => dest.Answers, opt => opt.MapFrom(src => src.Answers.Count))
                .ForMember((dest) => dest.Upvotes, opt => opt.MapFrom(src => src.Upvoters.Count));
            CreateMap<UpdateQuestionDto, Question>();
            CreateMap<Question, UpdateQuestionSolveStatusDto>();
        }
    }
}